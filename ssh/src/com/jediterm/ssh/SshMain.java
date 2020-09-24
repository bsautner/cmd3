package com.jediterm.ssh;

import com.jediterm.ssh.jsch.JSchShellTtyConnector;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.command.CommandListener;
import com.jediterm.terminal.ui.AbstractTerminalFrame;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author traff
 */
public class SshMain extends AbstractTerminalFrame {

    protected SshMain(CommandListener commandListener) {
        super(commandListener);
    }

    public static void main(final String[] arg) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        new SshMain(new CommandListener() {
            @Override
            public void commandEntered(@NotNull String command) {

            }

            @Override
            public void commandSelected(@NotNull String command) {

            }
        });
    }

    @Override
    public TtyConnector createTtyConnector() {
        return new JSchShellTtyConnector();
    }

}
