package edu.touro.mco152.bm;

import static edu.touro.mco152.bm.App.KILOBYTE;
import static edu.touro.mco152.bm.App.MEGABYTE;
import static edu.touro.mco152.bm.App.blockSizeKb;
import static edu.touro.mco152.bm.App.dataDir;
import static edu.touro.mco152.bm.App.msg;
import static edu.touro.mco152.bm.App.numOfBlocks;
import static edu.touro.mco152.bm.App.numOfMarks;
import static edu.touro.mco152.bm.App.testFile;

/**
 * A class storing all the units to be used for read and write tests.
 */
public class Units {

    int wUnitsComplete;
    int rUnitsComplete;
    int unitsComplete;
    float percentComplete;
    byte [] blockArr;
    int blockSize;
    int wUnitsTotal;
    int rUnitsTotal;
    int unitsTotal;

    public Units(){

        wUnitsComplete = 0;
        rUnitsComplete = 0;
        unitsComplete = 0;

        wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        unitsTotal = wUnitsTotal + rUnitsTotal;
        percentComplete = 0;

        blockSize = blockSizeKb * KILOBYTE;
        blockArr = new byte [blockSize];
        for (int b=0; b<blockArr.length; b++) {
            if (b%2==0) {
                blockArr[b]=(byte)0xFF;
            }
        }
    }

    public int getwUnitsComplete() {
        return wUnitsComplete;
    }

    public void setwUnitsComplete(int wUnitsComplete) {
        this.wUnitsComplete = wUnitsComplete;
    }

    public int getrUnitsComplete() {
        return rUnitsComplete;
    }

    public int getUnitsComplete() {
        return unitsComplete;
    }

    public void setUnitsComplete(int unitsComplete) {
        this.unitsComplete = unitsComplete;
    }

    public void setrUnitsComplete(int rUnitsComplete) {
        this.rUnitsComplete = rUnitsComplete;
    }

    public float getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(float percentComplete) {
        this.percentComplete = percentComplete;
    }

    public byte[] getBlockArr() {
        return blockArr;
    }

    public void setBlockArr(byte[] blockArr) {
        this.blockArr = blockArr;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getwUnitsTotal() {
        return wUnitsTotal;
    }

    public void setwUnitsTotal(int wUnitsTotal) {
        this.wUnitsTotal = wUnitsTotal;
    }

    public int getrUnitsTotal() {
        return rUnitsTotal;
    }

    public void setrUnitsTotal(int rUnitsTotal) {
        this.rUnitsTotal = rUnitsTotal;
    }

    public int getUnitsTotal() {
        return unitsTotal;
    }

    public void setUnitsTotal(int unitsTotal) {
        this.unitsTotal = unitsTotal;
    }
}
