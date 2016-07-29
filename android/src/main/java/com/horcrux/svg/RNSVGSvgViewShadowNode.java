/**
 * Copyright (c) 2015-present, Horcrux.
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */


package com.horcrux.svg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.UIViewOperationQueue;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Shadow node for RNSVG virtual tree root - RNSVGSvgView
 */
public class RNSVGSvgViewShadowNode extends LayoutShadowNode {

    private boolean mResponsible = false;

    private static final Map<String, Path> mDefinedClipPaths = new HashMap<>();

    @Nonnull private final RNSVGSvgViewManager viewManager;

    public RNSVGSvgViewShadowNode(@Nonnull final RNSVGSvgViewManager viewManager) {
        super();
        this.viewManager = viewManager;
    }

    @Override
    public void onCollectExtraUpdates(UIViewOperationQueue uiUpdater) {
        super.onCollectExtraUpdates(uiUpdater);
        uiUpdater.enqueueUpdateExtraData(getReactTag(), drawOutput());
    }

    private Object drawOutput() {
        Bitmap bitmap = Bitmap.createBitmap(
            (int) getLayoutWidth(),
            (int) getLayoutHeight(),
            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        drawChildren(canvas, paint);

        return bitmap;
    }

    /**
     * Draw all of the child nodes of this root node
     *
     * This method is synchronized since
     * {@link com.horcrux.svg.RNSVGImageShadowNode#loadBitmap(ImageRequest, Canvas, Paint)} calls it
     * asynchronously after images have loaded and are ready to be drawn.
     *
     * @param canvas
     * @param paint
     */
    public synchronized void drawChildren(Canvas canvas, Paint paint) {
        for (int i = 0; i < getChildCount(); i++) {
            RNSVGVirtualNode child = (RNSVGVirtualNode) getChildAt(i);
            child.setupDimensions(canvas);
            child.draw(canvas, paint, 1f);

            if (child.isResponsible() && !mResponsible) {
                mResponsible = true;
            }
        }
    }

    protected void invalidateView(@Nonnull final Rect dirtyRect) {
        final RNSVGSvgView svgView = this.viewManager.getSvgView();
        if (svgView != null) {
            final View rootView = svgView.getRootView();
            if (rootView != null) {
                rootView.invalidate(dirtyRect);
            }
        }
    }

    public void enableTouchEvents() {
        if (!mResponsible) {
            mResponsible = true;
        }
    }

    public int hitTest(Point point, ViewGroup view) {
        if (!mResponsible) {
            return -1;
        }

        int count = getChildCount();
        int viewTag = -1;
        for (int i = count - 1; i >= 0; i--) {
            viewTag = ((RNSVGVirtualNode) getChildAt(i)).hitTest(point, view.getChildAt(i));
            if (viewTag != -1) {
                break;
            }
        }

        return viewTag;
    }

    public void defineClipPath(Path clipPath, String clipPathRef) {
        mDefinedClipPaths.put(clipPathRef, clipPath);
    }

    // TODO: remove unmounted clipPath
    public void removeClipPath(String clipPathRef) {
        mDefinedClipPaths.remove(clipPathRef);
    }

    public Path getDefinedClipPath(String clipPathRef) {
        return mDefinedClipPaths.get(clipPathRef);
    }
}
