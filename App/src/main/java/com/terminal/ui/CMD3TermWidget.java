package com.terminal.ui;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.terminal.*;
import com.terminal.model.Cmd3Terminal;
import com.terminal.model.StyleState;
import com.terminal.model.TerminalTextBuffer;
import com.terminal.model.hyperlinks.HyperlinkFilter;
import com.terminal.model.hyperlinks.TextProcessing;
import com.terminal.ui.settings.SettingsProvider;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class CMD3TermWidget extends JPanel implements TerminalSession, TerminalWidget, TerminalActionProvider {
    private static final Logger LOG = Logger.getLogger(CMD3TermWidget.class);

    protected final TerminalPanel myTerminalPanel;
    protected final JScrollBar myScrollBar;
    protected final Cmd3Terminal myTerminal;
    protected final AtomicBoolean mySessionRunning = new AtomicBoolean();
    private SearchComponent myFindComponent;
    private final PreConnectHandler myPreConnectHandler;
    private TtyConnector myTtyConnector;
    private TerminalStarter myTerminalStarter;
    private Thread myEmuThread;
    protected final SettingsProvider mySettingsProvider;
    private TerminalActionProvider myNextActionProvider;
    private JLayeredPane myInnerPanel;
    private final TextProcessing myTextProcessing;
    private final List<TerminalWidgetListener> myListeners = new CopyOnWriteArrayList<>();

    public CMD3TermWidget(@NotNull SettingsProvider settingsProvider) {
        this(80, 24, settingsProvider);
    }


    public CMD3TermWidget(int columns, int lines, SettingsProvider settingsProvider) {
        super(new BorderLayout());

        mySettingsProvider = settingsProvider;

        StyleState styleState = createDefaultStyle();

        myTextProcessing = new TextProcessing(settingsProvider.getHyperlinkColor(),
                settingsProvider.getHyperlinkHighlightingMode());

        TerminalTextBuffer terminalTextBuffer = new TerminalTextBuffer(columns, lines, styleState, settingsProvider.getBufferMaxLinesCount(), myTextProcessing);
        myTextProcessing.setTerminalTextBuffer(terminalTextBuffer);

        myTerminalPanel = createTerminalPanel(mySettingsProvider, styleState, terminalTextBuffer);
        myTerminal = new Cmd3Terminal(myTerminalPanel, terminalTextBuffer, styleState);

        myTerminal.setModeEnabled(TerminalMode.AltSendsEscape, mySettingsProvider.altSendsEscape());

        myTerminalPanel.addTerminalMouseListener(myTerminal);

        myTerminalPanel.setNextProvider(this);
        myTerminalPanel.setCoordAccessor(myTerminal);

        myPreConnectHandler = createPreConnectHandler(myTerminal);
        myTerminalPanel.addCustomKeyListener(myPreConnectHandler);
        myScrollBar = createScrollBar();

        myInnerPanel = new JLayeredPane();
        myInnerPanel.setFocusable(false);
        setFocusable(false);

        myInnerPanel.setLayout(new TerminalLayout());
        myInnerPanel.add(myTerminalPanel, TerminalLayout.TERMINAL);
        myInnerPanel.add(myScrollBar, TerminalLayout.SCROLL);

        add(myInnerPanel, BorderLayout.CENTER);

        myScrollBar.setModel(myTerminalPanel.getBoundedRangeModel());
        mySessionRunning.set(false);

        myTerminalPanel.init();

        myTerminalPanel.setVisible(true);
    }

    public void addCustomKeyListener(KeyListener keyListener) {
        myTerminalPanel.addCustomKeyListener(keyListener);
    }

    protected JScrollBar createScrollBar() {
        JScrollBar scrollBar = new JScrollBar();
        scrollBar.setUI(new FindResultScrollBarUI());
        return scrollBar;
    }

    protected StyleState createDefaultStyle() {
        StyleState styleState = new StyleState();
        styleState.setDefaultStyle(mySettingsProvider.getDefaultStyle());
        return styleState;
    }

    protected TerminalPanel createTerminalPanel(@NotNull SettingsProvider settingsProvider, @NotNull StyleState styleState, @NotNull TerminalTextBuffer terminalTextBuffer) {
        return new TerminalPanel(settingsProvider, terminalTextBuffer, styleState);
    }

    protected PreConnectHandler createPreConnectHandler(Cmd3Terminal terminal) {
        return new PreConnectHandler(terminal);
    }

    public TerminalDisplay getTerminalDisplay() {
        return getTerminalPanel();
    }

    public TerminalPanel getTerminalPanel() {
        return myTerminalPanel;
    }

    public void setTtyConnector(@NotNull TtyConnector ttyConnector) {
        myTtyConnector = ttyConnector;

        myTerminalStarter = createTerminalStarter(myTerminal, myTtyConnector);
        myTerminalPanel.setTerminalStarter(myTerminalStarter);
    }

    protected TerminalStarter createTerminalStarter(Cmd3Terminal terminal, TtyConnector connector) {
        return new TerminalStarter(terminal, connector, new TtyBasedArrayDataStream(connector));
    }

    @Override
    public TtyConnector getTtyConnector() {
        return myTtyConnector;
    }

    @Override
    public Terminal getTerminal() {
        return myTerminal;
    }

    @Override
    public String getSessionName() {
        if (myTtyConnector != null) {
            return myTtyConnector.getName();
        } else {
            return "Session";
        }
    }

    public void start() {
        if (!mySessionRunning.get()) {
            myEmuThread = new Thread(new EmulatorTask());
            myEmuThread.start();
        } else {
            LOG.error("Should not try to start session again at this point... ");
        }
    }

    public void stop() {
        if (mySessionRunning.get() && myEmuThread != null) {
            myEmuThread.interrupt();
        }
    }

    public boolean isSessionRunning() {
        return mySessionRunning.get();
    }



    @Override
    public TerminalTextBuffer getTerminalTextBuffer() {
        return myTerminalPanel.getTerminalTextBuffer();
    }

    @Override
    public boolean requestFocusInWindow() {
        SwingUtilities.invokeLater(() -> myTerminalPanel.requestFocusInWindow());
        return super.requestFocusInWindow();
    }

    @Override
    public void requestFocus() {
        myTerminalPanel.requestFocus();
    }

    public boolean canOpenSession() {
        return !isSessionRunning();
    }

    @Override
    public void setTerminalPanelListener(TerminalPanelListener terminalPanelListener) {
        myTerminalPanel.setTerminalPanelListener(terminalPanelListener);
    }

    @Override
    public TerminalSession getCurrentSession() {
        return this;
    }

    @Override
    public CMD3TermWidget createTerminalSession(TtyConnector ttyConnector) {
        setTtyConnector(ttyConnector);
        return this;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void close() {
        stop();
        if (myTerminalStarter != null) {
            myTerminalStarter.close();
        }
        myTerminalPanel.dispose();
    }

    @Override
    public List<TerminalAction> getActions() {
        return Lists.newArrayList(new TerminalAction(mySettingsProvider.getFindActionPresentation(),
                new Predicate<KeyEvent>() {
                    @Override
                    public boolean apply(KeyEvent input) {
                        showFindText();
                        return true;
                    }
                }).withMnemonicKey(KeyEvent.VK_F));
    }

    private void showFindText() {
        if (myFindComponent == null) {
            myFindComponent = createSearchComponent();

            final JComponent component = myFindComponent.getComponent();
            myInnerPanel.add(component, TerminalLayout.FIND);
            myInnerPanel.moveToFront(component);
            myInnerPanel.revalidate();
            myInnerPanel.repaint();
            component.requestFocus();

            myFindComponent.addDocumentChangeListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    textUpdated();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    textUpdated();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    textUpdated();
                }

                private void textUpdated() {
                    findText(myFindComponent.getText(), myFindComponent.ignoreCase());
                }
            });

            myFindComponent.addIgnoreCaseListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    findText(myFindComponent.getText(), myFindComponent.ignoreCase());
                }
            });

            myFindComponent.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        myInnerPanel.remove(component);
                        myInnerPanel.revalidate();
                        myInnerPanel.repaint();
                        myFindComponent = null;
                        myTerminalPanel.setFindResult(null);
                        myTerminalPanel.requestFocusInWindow();
                    } else if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER || keyEvent.getKeyCode() == KeyEvent.VK_UP) {
                        myFindComponent.nextFindResultItem(myTerminalPanel.selectNextFindResultItem());
                    } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
                        myFindComponent.prevFindResultItem(myTerminalPanel.selectPrevFindResultItem());
                    } else {
                        super.keyPressed(keyEvent);
                    }
                }
            });
        } else {
            myFindComponent.getComponent().requestFocusInWindow();
        }
    }

    protected SearchComponent createSearchComponent() {
        return new SearchPanel();
    }

    protected interface SearchComponent {
        String getText();

        boolean ignoreCase();

        JComponent getComponent();

        void addDocumentChangeListener(DocumentListener listener);

        void addKeyListener(KeyListener listener);

        void addIgnoreCaseListener(ItemListener listener);

        void onResultUpdated(SubstringFinder.FindResult results);

        void nextFindResultItem(SubstringFinder.FindResult.FindItem selectedItem);

        void prevFindResultItem(SubstringFinder.FindResult.FindItem selectedItem);
    }

    private void findText(String text, boolean ignoreCase) {
        SubstringFinder.FindResult results = myTerminal.searchInTerminalTextBuffer(text, ignoreCase);
        myTerminalPanel.setFindResult(results);
        myFindComponent.onResultUpdated(results);
        myScrollBar.repaint();
    }

    @Override
    public TerminalActionProvider getNextProvider() {
        return myNextActionProvider;
    }

    public void setNextProvider(TerminalActionProvider actionProvider) {
        this.myNextActionProvider = actionProvider;
    }

    class EmulatorTask implements Runnable {
        public void run() {
            try {
                mySessionRunning.set(true);
                Thread.currentThread().setName("Connector-" + myTtyConnector.getName());
                if (myTtyConnector.init(myPreConnectHandler)) {
                    myTerminalPanel.addCustomKeyListener(myTerminalPanel.getTerminalKeyListener());
                    myTerminalPanel.removeCustomKeyListener(myPreConnectHandler);
                    myTerminalStarter.start();
                }
            } catch (Exception e) {
                LOG.error("Exception running terminal", e);
            } finally {
                try {
                    myTtyConnector.close();
                } catch (Exception e) {
                }
                mySessionRunning.set(false);
                TerminalPanelListener terminalPanelListener = myTerminalPanel.getTerminalPanelListener();
                if (terminalPanelListener != null)
                    terminalPanelListener.onSessionChanged(getCurrentSession());
                for (TerminalWidgetListener listener : myListeners) {
                    listener.allSessionsClosed(CMD3TermWidget.this);
                }
                myTerminalPanel.addCustomKeyListener(myPreConnectHandler);
                myTerminalPanel.removeCustomKeyListener(myTerminalPanel.getTerminalKeyListener());
            }
        }
    }

    public TerminalStarter getTerminalStarter() {
        return myTerminalStarter;
    }

    public class SearchPanel extends JPanel implements SearchComponent {

        private final JTextField myTextField = new JTextField();
        private final JLabel label = new JLabel();
        private final JButton prev;
        private final JButton next;
        private final JCheckBox ignoreCaseCheckBox = new JCheckBox("Ignore Case", true);

        public SearchPanel() {
            next = createNextButton();
            next.addActionListener(e -> nextFindResultItem(myTerminalPanel.selectNextFindResultItem()));

            prev = createPrevButton();
            prev.addActionListener(e -> prevFindResultItem(myTerminalPanel.selectPrevFindResultItem()));

            myTextField.setPreferredSize(new Dimension(
                    myTerminalPanel.myCharSize.width * 30,
                    myTerminalPanel.myCharSize.height + 3));
            myTextField.setEditable(true);

            updateLabel(null);

            add(myTextField);
            add(ignoreCaseCheckBox);
            add(label);
            add(next);
            add(prev);

            setOpaque(true);
        }

        protected JButton createNextButton() {
            return new BasicArrowButton(SwingConstants.NORTH);
        }

        protected JButton createPrevButton() {
            return new BasicArrowButton(SwingConstants.SOUTH);
        }

        @Override
        public void nextFindResultItem(SubstringFinder.FindResult.FindItem selectedItem) {
            updateLabel(selectedItem);
        }

        @Override
        public void prevFindResultItem(SubstringFinder.FindResult.FindItem selectedItem) {
            updateLabel(selectedItem);
        }

        private void updateLabel(SubstringFinder.FindResult.FindItem selectedItem) {
            SubstringFinder.FindResult result = myTerminalPanel.getFindResult();
            label.setText(((selectedItem != null) ? selectedItem.getIndex() : 0)
                    + " of " + ((result != null) ? result.getItems().size() : 0));
        }

        @Override
        public void onResultUpdated(SubstringFinder.FindResult results) {
            updateLabel(null);
        }

        @Override
        public String getText() {
            return myTextField.getText();
        }

        @Override
        public boolean ignoreCase() {
            return ignoreCaseCheckBox.isSelected();
        }

        @Override
        public JComponent getComponent() {
            return this;
        }

        public void requestFocus() {
            myTextField.requestFocus();
        }

        @Override
        public void addDocumentChangeListener(DocumentListener listener) {
            myTextField.getDocument().addDocumentListener(listener);
        }

        @Override
        public void addKeyListener(KeyListener listener) {
            myTextField.addKeyListener(listener);
        }

        @Override
        public void addIgnoreCaseListener(ItemListener listener) {
            ignoreCaseCheckBox.addItemListener(listener);
        }

    }

    private class FindResultScrollBarUI extends BasicScrollBarUI {

        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            super.paintTrack(g, c, trackBounds);

            SubstringFinder.FindResult result = myTerminalPanel.getFindResult();
            if (result != null) {
                int modelHeight = scrollbar.getModel().getMaximum() - scrollbar.getModel().getMinimum();
                int anchorHeight = Math.max(2, trackBounds.height / modelHeight);

                Color color = mySettingsProvider.getTerminalColorPalette()
                        .getColor(mySettingsProvider.getFoundPatternColor().getBackground());
                g.setColor(color);
                for (SubstringFinder.FindResult.FindItem r : result.getItems()) {
                    int where = trackBounds.height * r.getStart().y / modelHeight;
                    g.fillRect(trackBounds.x, trackBounds.y + where, trackBounds.width, anchorHeight);
                }
            }
        }

    }

    private static class TerminalLayout implements LayoutManager {
        public static final String TERMINAL = "TERMINAL";
        public static final String SCROLL = "SCROLL";
        public static final String FIND = "FIND";

        private Component terminal;
        private Component scroll;
        private Component find;

        @Override
        public void addLayoutComponent(String name, Component comp) {
            if (TERMINAL.equals(name)) {
                terminal = comp;
            } else if (FIND.equals(name)) {
                find = comp;
            } else if (SCROLL.equals(name)) {
                scroll = comp;
            } else throw new IllegalArgumentException("unknown component name " + name);
        }

        @Override
        public void removeLayoutComponent(Component comp) {
            if (comp == terminal) {
                terminal = null;
            }
            if (comp == scroll) {
                scroll = null;
            }
            if (comp == find) {
                find = comp;
            }
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            synchronized (target.getTreeLock()) {
                Dimension dim = new Dimension(0, 0);

                if (terminal != null) {
                    Dimension d = terminal.getPreferredSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height = Math.max(d.height, dim.height);
                }

                if (scroll != null) {
                    Dimension d = scroll.getPreferredSize();
                    dim.width += d.width;
                    dim.height = Math.max(d.height, dim.height);
                }

                if (find != null) {
                    Dimension d = find.getPreferredSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height = Math.max(d.height, dim.height);
                }

                Insets insets = target.getInsets();
                dim.width += insets.left + insets.right;
                dim.height += insets.top + insets.bottom;

                return dim;
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            synchronized (target.getTreeLock()) {
                Dimension dim = new Dimension(0, 0);

                if (terminal != null) {
                    Dimension d = terminal.getMinimumSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height = Math.max(d.height, dim.height);
                }

                if (scroll != null) {
                    Dimension d = scroll.getPreferredSize();
                    dim.width += d.width;
                    dim.height = Math.max(d.height, dim.height);
                }

                if (find != null) {
                    Dimension d = find.getMinimumSize();
                    dim.width = Math.max(d.width, dim.width);
                    dim.height = Math.max(d.height, dim.height);
                }

                Insets insets = target.getInsets();
                dim.width += insets.left + insets.right;
                dim.height += insets.top + insets.bottom;

                return dim;
            }
        }

        @Override
        public void layoutContainer(Container target) {
            synchronized (target.getTreeLock()) {
                Insets insets = target.getInsets();
                int top = insets.top;
                int bottom = target.getHeight() - insets.bottom;
                int left = insets.left;
                int right = target.getWidth() - insets.right;

                Dimension scrollDim = new Dimension(0, 0);
                if (scroll != null) {
                    scrollDim = scroll.getPreferredSize();
                    scroll.setBounds(right - scrollDim.width, top, scrollDim.width, bottom - top);
                }

                if (terminal != null) {
                    terminal.setBounds(left, top, right - left - scrollDim.width, bottom - top);
                }

                if (find != null) {
                    Dimension d = find.getPreferredSize();
                    find.setBounds(right - d.width - scrollDim.width, top, d.width, d.height);
                }
            }

        }
    }

    public void addHyperlinkFilter(HyperlinkFilter filter) {
        myTextProcessing.addHyperlinkFilter(filter);
    }

    @Override
    public void addListener(TerminalWidgetListener listener) {
        myListeners.add(listener);
    }

    @Override
    public void removeListener(TerminalWidgetListener listener) {
        myListeners.remove(listener);
    }


}
