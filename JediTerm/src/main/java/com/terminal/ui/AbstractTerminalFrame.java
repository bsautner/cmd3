package com.terminal.ui;


import com.terminal.TabbedTerminalWidget;
import com.terminal.TtyConnector;
import com.terminal.debug.BufferPanel;
import com.terminal.model.SelectionUtil;
import com.terminal.ui.settings.DefaultTabbedSettingsProvider;
import com.terminal.ui.settings.TabbedSettingsProvider;
import com.terminal.util.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;


public abstract class AbstractTerminalFrame {
    public static final Logger LOG = Logger.getLogger(AbstractTerminalFrame.class);

    private JFrame myBufferFrame;

    private final TerminalWidget myTerminal;

    public TerminalWidget getMyTerminal() {
        return myTerminal;
    }

    private final AbstractAction myOpenAction = new AbstractAction("New Session") {
        public void actionPerformed(final ActionEvent e) {
            openSession(myTerminal);
        }
    };

    private final AbstractAction myShowBuffersAction = new AbstractAction("Show buffers") {
        public void actionPerformed(final ActionEvent e) {
            if (myBufferFrame == null) {
                showBuffers();
            }
        }
    };

    private final AbstractAction myDumpDimension = new AbstractAction("Dump terminal dimension") {
        public void actionPerformed(final ActionEvent e) {
            LOG.info(myTerminal.getTerminalDisplay().getColumnCount() +
                    "x" + myTerminal.getTerminalDisplay().getRowCount());
        }
    };

    private final AbstractAction myDumpSelection = new AbstractAction("Dump selection") {
        public void actionPerformed(final ActionEvent e) {
            Pair<Point, Point> points = myTerminal.getTerminalDisplay()
                    .getSelection().pointsForRun(myTerminal.getTerminalDisplay().getColumnCount());
            LOG.info(myTerminal.getTerminalDisplay().getSelection() + " : '"
                    + SelectionUtil.getSelectionText(points.first, points.second, myTerminal.getCurrentSession().getTerminalTextBuffer()) + "'");
        }
    };

    private final AbstractAction myDumpCursorPosition = new AbstractAction("Dump cursor position") {
        public void actionPerformed(final ActionEvent e) {
            LOG.info(myTerminal.getCurrentSession().getTerminal().getCursorX() +
                    "x" + myTerminal.getCurrentSession().getTerminal().getCursorY());
        }
    };

    private final AbstractAction myCursor0x0 = new AbstractAction("1x1") {
        public void actionPerformed(final ActionEvent e) {
            myTerminal.getCurrentSession().getTerminal().cursorPosition(1, 1);
        }
    };

    private final AbstractAction myCursor10x10 = new AbstractAction("10x10") {
        public void actionPerformed(final ActionEvent e) {
            myTerminal.getCurrentSession().getTerminal().cursorPosition(10, 10);
        }
    };

    private final AbstractAction myCursor80x24 = new AbstractAction("80x24") {
        public void actionPerformed(final ActionEvent e) {
            myTerminal.getCurrentSession().getTerminal().cursorPosition(80, 24);
        }
    };

    private JMenuBar getJMenuBar() {
        final JMenuBar mb = new JMenuBar();
        final JMenu m = new JMenu("File");

        m.add(myOpenAction);
        mb.add(m);
        final JMenu dm = new JMenu("Debug");

        JMenu logLevel = new JMenu("Set log level ...");
        Level[] levels = new Level[]{Level.ALL, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL, Level.OFF};
        for (final Level l : levels) {
            logLevel.add(new AbstractAction(l.toString()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Logger.getRootLogger().setLevel(l);
                }
            });
        }
        dm.add(logLevel);
        dm.addSeparator();

        dm.add(myShowBuffersAction);
        dm.addSeparator();
        dm.add(myDumpDimension);
        dm.add(myDumpSelection);
        dm.add(myDumpCursorPosition);

        JMenu cursorPosition = new JMenu("Set cursor position ...");
        cursorPosition.add(myCursor0x0);
        cursorPosition.add(myCursor10x10);
        cursorPosition.add(myCursor80x24);
        dm.add(cursorPosition);
        mb.add(dm);

        return mb;
    }

    @Nullable
    protected JediTermWidget openSession(TerminalWidget terminal) {
        if (terminal.canOpenSession()) {
            return openSession(terminal, createTtyConnector());
        }
        return null;
    }

    public JediTermWidget openSession(TerminalWidget terminal, TtyConnector ttyConnector) {
        JediTermWidget session = terminal.createTerminalSession(ttyConnector);
        session.start();
        return session;
    }

    public abstract TtyConnector createTtyConnector();

    protected AbstractTerminalFrame() {
        this.myTerminal = createTabbedTerminalWidget();


//        final JFrame frame = new JFrame("CMD3");
//
//        frame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(final WindowEvent e) {
//                System.exit(0);
//            }
//        });
//
//        final JMenuBar mb = getJMenuBar();
//        frame.setJMenuBar(mb);
//        sizeFrameForTerm(frame);
//
//
//        // frame.getContentPane().add("Center", myTerminal.getComponent());
//
//       // frame.getContentPane().add("Center", splitPane);
//
//        frame.setLocationRelativeTo(null);
//        frame.pack();
//        frame.setLocationByPlatform(false);
//        frame.setVisible(true);
//
//        frame.setResizable(true);

//        myTerminal.setTerminalPanelListener(new TerminalPanelListener() {
//            public void onPanelResize(final Dimension pixelDimension, final RequestOrigin origin) {
//                if (origin == RequestOrigin.Remote) {
//                    sizeFrameForTerm(frame);
//                }
//                frame.pack();
//            }
//
//            @Override
//            public void onSessionChanged(final TerminalSession currentSession) {
//                frame.setTitle(currentSession.getSessionName());
//            }
//
//            @Override
//            public void onTitleChanged(String title) {
//                frame.setTitle(myTerminal.getCurrentSession().getSessionName());
//            }
//        });

        openSession(myTerminal);
       // splitPane.setRightComponent(myTerminal.getComponent());

    }

    @NotNull
    protected AbstractTabbedTerminalWidget createTabbedTerminalWidget() {
        return new TabbedTerminalWidget(new DefaultTabbedSettingsProvider(), this::openSession) {
            @Override
            public JediTermWidget createInnerTerminalWidget() {
                return createTerminalWidget(getSettingsProvider());
            }
        };
    }

    protected JediTermWidget createTerminalWidget(@NotNull TabbedSettingsProvider settingsProvider) {
        return new JediTermWidget(settingsProvider);
    }

    private void sizeFrameForTerm(final JFrame frame) {
        SwingUtilities.invokeLater(() -> {
            Dimension d = myTerminal.getPreferredSize();

            d.width += frame.getWidth() - frame.getContentPane().getWidth();
            d.height += frame.getHeight() - frame.getContentPane().getHeight();
            frame.setSize(d);
        });
    }

    private void showBuffers() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                myBufferFrame = new JFrame("buffers");
                final JPanel panel = new BufferPanel(myTerminal.getCurrentSession());

                myBufferFrame.getContentPane().add(panel);
                myBufferFrame.pack();
                myBufferFrame.setLocationByPlatform(true);
                myBufferFrame.setVisible(true);
                myBufferFrame.setSize(800, 600);

                myBufferFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(final WindowEvent e) {
                        myBufferFrame = null;
                    }
                });
            }
        });
    }

    public void sendCommand(@NotNull String command)   {

        try {
            myTerminal.getCurrentSession().getTtyConnector().write(command);
            myTerminal.getCurrentSession().getTtyConnector().write("\n");
            myTerminal.grabFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // myTerminal.sendCommand(command);
    }
}
