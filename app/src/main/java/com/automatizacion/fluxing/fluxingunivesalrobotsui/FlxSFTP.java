/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.os.AsyncTask;

import com.jcraft.jsch.*;

import java.io.File;
import java.util.Vector;

/**
 *
 * @author Chava
 */
public class FlxSFTP {
    private final JSch jsch = new JSch();
    private Session session;
    private ChannelSftp sftp;
    public boolean Busy = false;
    private boolean ret = false;
    String log = "";
    String username = "";
    String password = "";
    String host = "";
    
    FlxSFTP()
    {
    }

    public void Connect() {
        if (Busy) return;
        Busy = true;
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                LogWriteLn("");
                try {
                    session = jsch.getSession(username, host, 22);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.setPassword(password);
                    LogWriteLn("Connecting...");
                    session.connect();
                    sftp = (ChannelSftp) session.openChannel("sftp");
                    sftp.connect();
                    LogWriteLn("Connected");
                } catch (JSchException | NumberFormatException e) {
                    LogWriteLn("Error: " + e.getMessage());
                    Disconnect();
                }
                Busy = false;
                return null;
            }
        }.execute(1);
    }

    public void Disconnect() {
        LogWriteLn("");
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return;
        }
        try {
            if (session == null || sftp == null) return;
            sftp.exit();
            sftp.disconnect();
            session.disconnect();
        } catch (Exception e) {
            LogWriteLn("Error: Could not disconnect from the host");
        }
    }
    
    public void DisconnectAsync() {
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return;
        }
        if (Busy) return;
        Busy = true;
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                LogWriteLn("");
                try {
                    if (session == null || sftp == null) {
                        Busy = false;
                        return null;
                    }
                    LogWriteLn("Disconnecting...");
                    sftp.exit();
                    sftp.disconnect();
                    session.disconnect();
                    LogWriteLn("Disconnected");
                } catch (Exception e) {
                    LogWriteLn("Error: Could not disconnect from the host");
                }
                Busy = false;
                return null;
            }
        }.execute(1);
        WaitTask();
    }

    public boolean IsConnected() {
        if(sftp == null) return false;
        return sftp.isConnected();
    }

    public String GetCurrentLocalPath() {
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return "";
        }
        try {
            return sftp.lpwd();
        } catch (Exception e) {
            LogWriteLn("Error: " + e.getMessage());
        }
        return "";
    }
    
    public String GetCurrentLocalPathAsync() {
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return "";
        }
        if (Busy) return "";
        Busy = true;
        final String[] ret = {""};
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    ret[0] = sftp.lpwd();
                } catch (Exception e) {
                    LogWriteLn("Error: " + e.getMessage());
                }
                Busy = false;
                return null;
            }
        }.execute(1);
        WaitTask();
        return ret[0];
    }

    public String GetCurrentRemotePath() {
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return "";
        }
        try {
            return sftp.pwd();
        } catch (Exception e) {
            LogWriteLn("Error: " + e.getMessage());
        }
        return "";
    }
    
    public String GetCurrentRemotePathAsync() {
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return "";
        }
        if (Busy) return "";
        Busy = true;
        final String[] ret = {""};
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    ret[0] = sftp.pwd();
                } catch (SftpException e) {
                    LogWriteLn("Error: " + e.getMessage());
                }
                Busy = false;
                return null;
            }
        }.execute(1);
        WaitTask();
        return ret[0];
    }

    public String ReadDirectoryContent()
    {
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return "";
        }
        String ret = "";
        try
        {
            Vector files = sftp.ls(GetCurrentRemotePath());
            int Count = 0;
            for(int i=0; i<files.size();i++)
            {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) files.get(i);
                ret += entry.getFilename() + "\t\t";
                Count++;
                if(Count > 5)
                {
                    ret += "\n";
                    Count = 0;
                }
            }
        }
        catch(SftpException e)
        {
            LogWriteLn("Error: " + e.getMessage());
        }
        ret += "\n";
        return ret;
    }
    
    public String ReadDirectoryContentAsync() {
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return "";
        }
        if (Busy) return "";
        Busy = true;
        final String[] ret = {""};
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    LogWriteLn(sftp.pwd());
                    Vector files = sftp.ls(sftp.pwd());
                    int Count = 0;
                    for (int i = 0; i < files.size(); i++) {
                        ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) files.get(i);
                        ret[0] += entry.getFilename() + "\t\t";
                        Count++;
                        if (Count > 5) {
                            ret[0] += "\n";
                            Count = 0;
                        }
                    }
                } catch (SftpException e) {
                    LogWriteLn("Error: " + e.getMessage());
                }
                ret[0] += "\n";
                Busy = false;
                return null;
            }
        }.execute(1);
        WaitTask();
        return ret[0];
    }

    public boolean ChangeDirectory(String Path)
    {
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return false;
        }
        LogWriteLn("");
        try
        {
            sftp.cd(Path);
            LogWriteLn(GetCurrentRemotePath());
        }
        catch(SftpException e)
        {
            LogWriteLn("Error: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    public boolean ChangeDirectoryAsync(final String Path) {
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return false;
        }
        if (Busy) return false;
        Busy = true;
        ret = false;
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                LogWriteLn("");
                try {
                    sftp.cd(Path);
                    LogWriteLn(GetCurrentRemotePath());
                    ret = true;
                } catch (SftpException e) {
                    LogWriteLn("Error: " + e.getMessage());
                    ret = false;
                }
                Busy = false;
                return null;
            }
        }.execute(1);
        WaitTask();
        return ret;
    }

    public boolean SendFile(String LocalPath, String RemoteName)
    {
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return false;
        }
        LogWriteLn("");
        try
        {
            sftp.put(LocalPath, RemoteName);
            LogWriteLn("File sended!");
        }
        catch(SftpException e)
        {
            LogWriteLn("Error: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    public void SendFileAsync(final String LocalPath, final String RemoteName) {
        if (!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return;
        }
        if (Busy) return;
        Busy = true;
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                LogWriteLn("");
                try {
                    LogWriteLn("Sending...");
                    sftp.put(LocalPath, RemoteName);
                    LogWriteLn("File sended!");
                } catch (SftpException e) {
                    LogWriteLn("Error: " + e.toString());
                }
                Busy = false;
                return null;
            }
        }.execute(1);
        WaitTask();
    }

    public boolean ReceiveFile(String RemoteName, String LocalPath) {
        if (!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return false;
        }
        LogWriteLn("");
        try {
            LogWriteLn("Receiving file...");
            sftp.get(RemoteName, LocalPath);
            LogWriteLn("File received!");
        } catch (SftpException e) {
            LogWriteLn("Error: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    public void ReceiveFileAsync(final String RemoteName, final String LocalPath) {
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return;
        }
        if (Busy) return;
        Busy = true;
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                LogWriteLn("");
                try {
                    sftp.get(RemoteName, LocalPath);
                    LogWriteLn("File received!");
                } catch (SftpException e) {
                    LogWriteLn("Error: " + e.getMessage());
                }
                Busy = false;
                return null;
            }
        }.execute(1);
        WaitTask();
    }

    public Vector GetFilesByExtension(final String Extension) {
        if(!IsConnected()) {
            LogWriteLn("Error: Doesn't exists connection with the server");
            return new Vector();
        }
        if (Busy) return new Vector();
        Busy = true;
        final Vector FileNames = new Vector();
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    LogWriteLn(sftp.pwd());
                    Vector files = sftp.ls(sftp.pwd());
                    int Count = 0;
                    for (int i = 0; i < files.size(); i++) {
                        ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) files.get(i);
                        if(entry.getFilename().endsWith(Extension))
                            FileNames.add(entry.getFilename());
                    }
                } catch (SftpException e) {
                    LogWriteLn("Error: " + e.getMessage());
                }
                Busy = false;
                return null;
            }
        }.execute(1);
        WaitTask();
        return FileNames;
    }

    public void WaitTask()
    {
        while(Busy == true) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void LogWrite(String Text)
    {
        log += Text;
    }
    
    private void LogWriteLn(String Text)
    {
        log += Text + "\n";
    }
    
    public String ReadLog()
    {
        String ret = log;
        log = "";
        return ret;
    }
    
    public void FlushLog()
    {
        log = "";
    }
}
