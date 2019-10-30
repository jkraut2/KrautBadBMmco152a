package edu.touro.mco152.bm;

/**
 * Interface that all types of marks will be able to implement in the company's future.
 */
public interface MarkInterface {

    String getBwMbSecAsString();
    String getMinAsString();
    String getMaxAsString();
    String getAvgAsString();
    int getMarkNum();
    void setMarkNum(int markNum);
    double getBwMbSec();
    void setBwMbSec(double bwMbSec);
    double getCumAvg();
    void setCumAvg(double cumAvg);
    double getCumMin();
    void setCumMin(double cumMin);
    double getCumMax();
    void setCumMax(double cumMax);


}