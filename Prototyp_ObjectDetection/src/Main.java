import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.util.List;

public class Main {

    public void detectAndDisplay(Mat frame, CascadeClassifier shapeCascade){
        Mat frameGray = new Mat();
        Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frameGray, frameGray);

        MatOfRect shapes = new MatOfRect();
        shapeCascade.detectMultiScale(frameGray, shapes);

        List<Rect> listOfShapes = shapes.toList();
        for (Rect shape : listOfShapes){
            Point center = new Point(shape.x + shape.width/2, shape.y + shape.height/2);
            Imgproc.ellipse(frame, center, new Size(shape.width/2, shape.height/2), 0,0,360, new Scalar(255,0,255));

            Mat shapeROI = frameGray.submat(shape);
        }
        HighGui.imshow("Capture - Shape detection", frame);
    }

    public void run (String [] args){
        String filenameShapeCascade = args.length > 2 ? args[0]: "...";

        CascadeClassifier shapeCascade = new CascadeClassifier();

        if (!shapeCascade.load(filenameShapeCascade)){
            System.err.println("--(!)Error loading shape cascade: " + filenameShapeCascade);
            System.exit(0);
        }

        VideoCapture capture = new VideoCapture(cameraDevice);
        if (!capture.isOpened()) {
            System.err.println("--(!)Error opening video capture");
            System.exit(0);
        }

        Mat frame = new Mat();
        while (capture.read(frame)) {
            if (frame.empty()) {
                System.err.println("--(!) No captured frame -- Break!");
                break;
            }
            //-- 3. Apply the classifier to the frame
            detectAndDisplay(frame, shapeCascade);
            if (HighGui.waitKey(10) == 27) {
                break;// escape
            }
        }
        System.exit(0);
    }



    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new Main().run(args);
    }
}
