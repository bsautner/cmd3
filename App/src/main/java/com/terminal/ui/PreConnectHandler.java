package com.terminal.ui;


import com.terminal.Terminal;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PreConnectHandler implements KeyListener {
    private final Object mySync = new Object();
    private Terminal myTerminal;
    private StringBuffer myAnswer;
    private boolean myVisible;

    public PreConnectHandler(Terminal terminal) {
        this.myTerminal = terminal;
        this.myVisible = true;
    }




    public void keyPressed(KeyEvent e) {
        if (myAnswer == null) return;
        synchronized (mySync) {
            boolean release = false;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_BACK_SPACE:
                    if (myAnswer.length() > 0) {
                        myTerminal.backspace();
                        myTerminal.eraseInLine(0);
                        myAnswer.deleteCharAt(myAnswer.length() - 1);
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    myTerminal.nextLine();
                    release = true;
                    break;
            }

            if (release) mySync.notifyAll();
        }

    }

    public void keyReleased(KeyEvent e) {

    }

    public void keyTyped(KeyEvent e) {
        if (myAnswer == null) return;
        char c = e.getKeyChar();
        if (Character.getType(c) != Character.CONTROL) {
            if (myVisible) myTerminal.writeCharacters(Character.toString(c));
            myAnswer.append(c);
        }
    }


}
