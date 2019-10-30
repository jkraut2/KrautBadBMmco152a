
package edu.touro.mco152.bm.ui;

import static edu.touro.mco152.bm.App.KILOBYTE;
import static edu.touro.mco152.bm.App.MEGABYTE;
import static edu.touro.mco152.bm.App.blockSizeKb;
import static edu.touro.mco152.bm.App.dataDir;
import static edu.touro.mco152.bm.App.msg;
import static edu.touro.mco152.bm.App.numOfBlocks;
import static edu.touro.mco152.bm.App.numOfMarks;
import static edu.touro.mco152.bm.App.testFile;
import static edu.touro.mco152.bm.DiskMark.MarkType.READ;
import static edu.touro.mco152.bm.DiskMark.MarkType.WRITE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import edu.touro.mco152.bm.ui.Gui;
/**
 *
 *
 *make a gui interface for implementing all gui related methods.
 */

/**
 * Thread running the disk benchmarking. only one of these threads can run at
 * once.
 */
public class DiskWorker  {
    Units units = new Units();
    @Override
    protected Boolean doBenchmark() throws Exception {
        
        System.out.println("*** starting new worker thread");
        msg("Running readTest "+App.readTest+"   writeTest "+App.writeTest);
        msg("num files: "+App.numOfMarks+", num blks: "+App.numOfBlocks
           +", blk size (kb): "+App.blockSizeKb+", blockSequence: "+App.blockSequence);
        

   
        DiskMark wMark, rMark;
        
        Gui.updateLegend();
        
        if (App.autoReset == true) {
            App.resetTestData();
            Gui.resetTestData();
        }
        
        int startFileNum = App.nextMarkNumber;
        
        if(App.writeTest) {
            writeTest();
        }

        
        if (App.readTest) {
            readTest();
            }

        }
        App.nextMarkNumber += App.numOfMarks;      
        return true;
    }
    


    /**
     * deals with write test(SRP)
     */
    public void writeTest() {
        DiskRun run = new DiskRun(DiskRun.IOMode.WRITE, App.blockSequence);
        setRunParams();

        msg("disk info: (" + run.getDiskInfo() + ")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());

        if (App.multiFile == false) {
            testFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
        }
        for (int m = startFileNum; m < startFileNum + App.numOfMarks && !isCancelled(); m++) {

            if (App.multiFile == true) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            wMark = new DiskMark(WRITE);
            measureData(wMark,m);
            App.updateMetrics(wMark);
            publish(wMark);

            setRunVars();}
        persistData();
        Gui.runPanel.addRun(run);


    }

    /**
     * deals with read test (SRP)
     */
    public void readTest(){
        DiskRun run = new DiskRun(DiskRun.IOMode.READ, App.blockSequence);
        setRunParams();



        msg("disk info: ("+ run.getDiskInfo()+")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());

        for (int m=startFileNum; m<startFileNum+App.numOfMarks && !isCancelled(); m++) {

            if (App.multiFile == true) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator+"testdata"+m+".jdm");
            }
            rMark = new DiskMark(READ);
            measureData(rMark,m);

            App.updateMetrics(rMark);
            publish(rMark);
            setRunVars();


        }

        persistData();
        Gui.runPanel.addRun(run);
    }

    /**
     * has task of checking whether data needs rest (SRP)
     */
    public void checkResetData(){
        if (App.autoReset == true) {
            App.resetTestData();
            Gui.resetTestData();
        }
    }

    /**
     * has the task of dealing with the data (SRP)
     */
    public void persistData() {
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();
    }

    /**
     * has task of run parameters (SRP)
     */
    public void setRunParams(){
        run.setNumMarks(App.numOfMarks);
        run.setNumBlocks(App.numOfBlocks);
        run.setBlockSize(App.blockSizeKb);
        run.setTxSize(App.targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));
    }

    /**
     * has the task of setting the run variables (SRP)
     */
    public void setRunVars(){
        run.setRunMax(rMark.getCumMax());
        run.setRunMin(rMark.getCumMin());
        run.setRunAvg(rMark.getCumAvg());
        run.setEndTime(new Date());
    }

    /**
     * has task of measuring the actual data (SRP)
     * @param d
     * @param m
     */
    public void measureData(DiskMark d, int m){
        if(d.type == DiskMark.MarkType.WRITE){
            d.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesWrittenInMark = 0;

            String mode = "rw";
            if (App.writeSyncEnable) {
                mode = "rwd";
            }

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, mode)) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks - 1);
                            rAccFile.seek(rLoc * units.getBlockSize());
                        } else {
                            rAccFile.seek(b * units.getBlockSize());
                        }
                        rAccFile.write(units.getBlockArr(), 0,units.getBlockSize());
                        totalBytesWrittenInMark += units.getBlockSize();
                        units.setwUnitsComplete(units.getwUnitsComplete() + 1);
                        units.setUnitsComplete(units.getrUnitsComplete + units.getwUnitsComplete);
                        units.setPercentComplete((float) unitsComplete / (float) unitsTotal * 100f);
                        setProgress((int) units.getPercentComplete());
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbWritten = (double) totalBytesWrittenInMark / (double) MEGABYTE;
            wMark.setBwMbSec(mbWritten / sec);
            msg("m:" + m + " write IO is " + d.getBwMbSecAsString() + " MB/s     "
                    + "(" + Util.displayString(mbWritten) + "MB written in "
                    + Util.displayString(sec) + " sec)");
        }
        else if(d.type == DiskMark.MarkType.READ)
        {
            d.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesReadInMark = 0;

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile,"r")) {
                    for (int b=0; b<numOfBlocks; b++) {
                        if (App.blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks-1);
                            rAccFile.seek(rLoc* units.getBlockSize());
                        } else {
                            rAccFile.seek(b* units.getBlockSize());
                        }
                        rAccFile.readFully(blockArr, 0, units.getBlockSize());
                        totalBytesReadInMark += units.getBlockSize();
                        setrUnitsComplete(units.getUnitsComplete() +1);
                        setUnitsComplete(getrUnitsComplete() + getwUnitsComplete());
                        setPercentComplete((float)unitsComplete/(float)unitsTotal * 100f);
                        setProgress((int)percentComplete);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double)elapsedTimeNs / (double)1000000000;
            double mbRead = (double) totalBytesReadInMark / (double) MEGABYTE;
            d.setBwMbSec(mbRead / sec);
            msg("m:"+m+" READ IO is "+rMark.getBwMbSec()+" MB/s    "
                    + "(MBread "+mbRead+" in "+sec+" sec)");
        }

    }
}
