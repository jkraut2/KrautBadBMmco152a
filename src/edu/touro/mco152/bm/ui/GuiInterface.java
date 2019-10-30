package edu.touro.mco152.bm;

/**
 * Interface that specificaly guis will be able to implement in the company's future. Helps organize gui related
 * methods scattered throughout the bad BM.
 */
public interface GuiInterface {

    public void resetTestData();
    public void updateLegend();
    public void addWriteMark(DiskMark mark):
    public void addReadMark(DiskMark mark);
    public void msg(String message);
    public void setLocation(String path);
    public void applyTestParams();
    public void clearMessages();
}