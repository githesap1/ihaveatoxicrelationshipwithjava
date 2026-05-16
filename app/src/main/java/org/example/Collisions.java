// CSE 1242 - Term Project
//MuctebaEnes_Kapusuz_150124083
// avoids sqrt by comparing squared distances

package org.example;

public class Collisions {

    public static boolean circleOverlap(double cx1, double cy1, double r1,
                                        double cx2, double cy2, double r2) {
        double dx = cx1 - cx2;
        double dy = cy1 - cy2;
        double sumR = r1 + r2;
        return dx * dx + dy * dy <= sumR * sumR;
    }
}
