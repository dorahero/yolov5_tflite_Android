package detection.tflite;

import android.content.res.AssetManager;

import java.io.IOException;

public class DetectorFactory {
    public static YoloV5ClassifierDetect getDetector(
            final AssetManager assetManager,
            final String modelFilename)
            throws IOException {
        String labelFilename = null;
        boolean isQuantized = false;
        int inputSize = 0;
        int[] output_width = new int[]{0};
        int[][] masks = new int[][]{{0}};
        int[] anchors = new int[]{0};

        if (modelFilename.equals("s_lp_160.tflite")) {
            labelFilename = "file:///android_asset/lplabel.txt";
            isQuantized = false;
            inputSize = 160;
            output_width = new int[]{20, 10, 5};
            masks = new int[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
            anchors = new int[]{
                    10,13, 16,30, 33,23, 30,61, 62,45, 59,119, 116,90, 156,198, 373,326
            };
        }

        if (modelFilename.equals("s_car8_320.tflite")) {
            labelFilename = "file:///android_asset/car8.txt";
            isQuantized = false;
            inputSize = 320;
            output_width = new int[]{40, 20, 10};
            masks = new int[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
            anchors = new int[]{
                    10,13, 16,30, 33,23, 30,61, 62,45, 59,119, 116,90, 156,198, 373,326
            };
        }


        return YoloV5ClassifierDetect.create(assetManager, modelFilename, labelFilename, isQuantized,
                inputSize, output_width, masks, anchors);
    }

}
