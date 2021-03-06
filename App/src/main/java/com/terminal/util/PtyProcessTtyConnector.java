package com.terminal.util;


import com.pty4j.PtyProcess;
import com.pty4j.WinSize;
import com.terminal.ProcessTtyConnector;
import com.terminal.ui.PreConnectHandler;

import java.nio.charset.Charset;


public class PtyProcessTtyConnector extends ProcessTtyConnector {
    private PtyProcess myProcess;

    public PtyProcessTtyConnector(PtyProcess process, Charset charset) {
        super(process, charset);

        myProcess = process;
    }

    @Override
    protected void resizeImmediately() {
        if (getPendingTermSize() != null && getPendingPixelSize() != null) {
            myProcess.setWinSize(
                    new WinSize(getPendingTermSize().width, getPendingTermSize().height, getPendingPixelSize().width, getPendingPixelSize().height));
        }
    }

    @Override
    public boolean isConnected() {
        return myProcess.isRunning();
    }

    @Override
    public boolean init(PreConnectHandler myPreConnectHandler) {
        return false;
    }

    @Override
    public String getName() {
        return "Local";
    }
}
