package com.android.launcher3.folder;

public class ClippedFolderIconLayoutRule implements FolderIcon.PreviewLayoutRule {

    static final int MAX_NUM_ITEMS_IN_PREVIEW = 9;
    private static final int MIN_NUM_ITEMS_IN_PREVIEW = 2;

    final float MIN_SCALE = 0.48f;
    final float MAX_SCALE = 0.58f;
    final float MAX_RADIUS_DILATION = 0.15f;
    final float ITEM_RADIUS_SCALE_FACTOR = 1.33f;

    private float[] mTmpPoint = new float[2];

    private float mAvailableSpace;
    private float mRadius;
    private float mIconSize;
    private boolean mIsRtl;
    private float mBaselineIconScale;

    @Override
    public void init(int availableSpace, int intrinsicIconSize, boolean rtl) {
        mAvailableSpace = availableSpace;
        mRadius = ITEM_RADIUS_SCALE_FACTOR * availableSpace / 2f;
        mIconSize = intrinsicIconSize;
        mIsRtl = rtl;
        mBaselineIconScale = availableSpace / (intrinsicIconSize * 1f);
    }

    public float getmAvailableSpace() {
        return mAvailableSpace;
    }

    @Override
    public FolderIcon.PreviewItemDrawingParams computePreviewItemDrawingParams(int index,
            int curNumItems, FolderIcon.PreviewItemDrawingParams params) {

        float totalScale = scaleForNumItems(curNumItems);
        float transX;
        float transY;
        float overlayAlpha = 0;

        // Items beyond those displayed in the preview are animated to the center
        if (index >= MAX_NUM_ITEMS_IN_PREVIEW) {
            transX = transY = mAvailableSpace / 2 - (mIconSize * totalScale) / 2;
        } else {
            getPosition(index, curNumItems, mTmpPoint);
            transX = mTmpPoint[0];
            transY = mTmpPoint[1];
        }

        if (params == null) {
            params = new FolderIcon.PreviewItemDrawingParams(transX, transY, totalScale, overlayAlpha);
        } else {
            params.update(transX, transY, totalScale);
            params.overlayAlpha = overlayAlpha;
        }
        return params;
    }

    private void getPosition(int index, int curNumItems, float[] result) {
        float halfIconSize = (mIconSize * scaleForNumItems(curNumItems)) / 2;
        int row = index / 3;
        int col = index - row * 3;
        float x, y;
        float x_step = (mAvailableSpace - 20) / 3;
        float y_step = (mAvailableSpace - 20) / 3;
        x = col * x_step + x_step / 2 - halfIconSize;
        y = row * y_step + y_step / 2 - halfIconSize;
        result[0] = x + 12;
        result[1] = y + 12;
    }

    private float scaleForNumItems(int numItems) {
        float scale;
        if(numItems == 1) {
            scale = MAX_SCALE;
        } else {
            scale = (float) (MIN_SCALE / 2);
        }

        return scale * mBaselineIconScale;
    }

    @Override
    public int numItems() {
        return MAX_NUM_ITEMS_IN_PREVIEW;
    }

    @Override
    public boolean clipToBackground() {
        return true;
    }

}
