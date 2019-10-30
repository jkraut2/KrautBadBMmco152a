package edu.touro.mco152.bm;

public class SwingWorkerImplemented extends SwingWorker <Boolean, DiskMark> {
    /**
     * A class to implement swingworker. This class is seperated from the diskworker class
     * but at this point sill relies on Diskworker.
     *
     */

    Boolean doInBackground() throws Exception {

        DiskWorker.doBenchmark();
    }

    @Override
    void process(List<DiskMark> markList) {
        markList.stream().forEach((m) -> {
            if (m.type == DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(m);
            } else {
                Gui.addReadMark(m);
            }
        });
    }

    @Override
     void done() {
        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
    }

}
