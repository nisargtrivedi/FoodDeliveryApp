package com.kukdudelivery.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.kukdudelivery.ApiController.OrderResponse;
import com.kukdudelivery.Model.Order_tbl;
import com.kukdudelivery.OrderDetails;
import com.kukdudelivery.PickOrderInterface;
import com.kukdudelivery.R;
import com.kukdudelivery.WebApi.WebServiceCaller;
import com.kukdudelivery.util.AppPreferences;
import com.kukdudelivery.util.DateUtils;
import com.kukdudelivery.util.EEditText;
import com.kukdudelivery.util.TTextView;
import com.kukdudelivery.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kukdudelivery.util.Utility.makeCall;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
    public List<String> list;
    Activity context;
    public String name;
    AppPreferences appPreferences;
    PickOrderInterface pickOrderInterface;

    private Animator currentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int shortAnimationDuration;

    public DocumentAdapter(Activity context, List<String> list) {
        this.context = context;
        this.list = list;
        appPreferences = new AppPreferences(context);

        shortAnimationDuration = context.getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }






    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgDoc;
        public ImageView thumb_button_1;
        public FrameLayout container;

        public ViewHolder(View view) {
            super(view);

            imgDoc = view.findViewById(R.id.expanded_image);
            thumb_button_1 = view.findViewById(R.id.thumb_button_1);
            container=view.findViewById(R.id.container);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.doc_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final String task = list.get(position);

        if (task != null && !task.isEmpty()) {

            Picasso.with(context).load(task).into(holder.thumb_button_1);
            holder.thumb_button_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showPhoto(task);
                    zoomImageFromThumb(holder.thumb_button_1,task,holder.imgDoc,holder.container);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }

    public void showPhoto(String str) {
        try {
            View view = context.getLayoutInflater().inflate(R.layout.img_expand, null);
            final ImageView edtName = view.findViewById(R.id.img);
            final android.app.AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(view)
                    .create();
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            dialog.show();
            Picasso.with(context).load(str).into(edtName);

        } catch (Exception ex) {
            System.out.println("Edit Profile --> " + ex.toString());

        }
    }


    private void zoomImageFromThumb(final View thumbView, String str, final ImageView img, FrameLayout container) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
//        final ImageView expandedImageView = (ImageView) findViewById(
//                R.id.expanded_image);
//        expandedImageView.setImageResource(imageResId);

        Picasso.with(context).load(str).into(img);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        container.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        img.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        img.setPivotX(0f);
        img.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(img, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(img, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(img, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(img,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(img, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(img,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(img,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(img,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        img.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        img.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }
}
