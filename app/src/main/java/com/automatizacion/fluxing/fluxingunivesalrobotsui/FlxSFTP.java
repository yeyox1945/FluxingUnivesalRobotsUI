/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.os.AsyncTask;

import com.jcraft.jsch.*;
import java.util.Vector;

/**
 *
 * @author Chava
 */
public class FlxSFTP {
    public enum FlxMethod { Connect, Disconnect, GetCurrentLocalPath, GetCurrentRemotePath,
                        ReadDirectoryContent, ChangeDirectory, SendFile, ReceiveFile }
    private final JSch jsch = new JSch();
    private Session session;
    private ChannelSftp sftp;
    public boolean Connected = false;
    public boolean Busy = false;
    public String Result = "";
    String log = "";
    String username = "";
    String password = "";
    String host = "";
    
    FlxSFTP()
    {
    }

    public boolean Connect()
    {
        LogWriteLn("");
        try
        {
            // connecting to the host
            session = jsch.getSession(username, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            //session.setPassword(new String(PF_Password.getPassword()));
            session.setPassword(password);
            LogWriteLn("Connecting...");
            session.connect();
            sftp = (ChannelSftp)session.openChannel("sftp");
            sftp.connect();
            LogWriteLn("Connected");
            return true;
        }
        catch (JSchException | NumberFormatException e)
        {
            LogWriteLn("Error: " + e.getMessage());
            Disconnect();
            return false;
        }
    }
    
    public boolean Disconnect()
    {
        LogWriteLn("");
        try
         {
            if(session == null || sftp == null) return true;
            sftp.exit();
            sftp.disconnect();
            session.disconnect();
            return true;
         }
         catch (Exception e)
         {
                LogWriteLn("Error: Could not disconnect from the host");
                return false;
         }
    }
    
    public String GetCurrentLocalPath()
    {
         try
        {
            return sftp.lpwd();
        }
        catch(Exception e)
        {
            LogWriteLn("Error: " + e.getMessage());
        }
         return "";
    }
    
    public String GetCurrentRemotePath()
    {
         try
        {
            return sftp.pwd();
        }
        catch(SftpException e)
        {
            LogWriteLn("Error: " + e.getMessage());
        }
         return "";
    }
    
    public String ReadDirectoryContent()
    {
        String ret = "";
        try
        {
            LogWriteLn(sftp.pwd());
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
            return "";
        }
        return ret + "\n";
    }
    
    public boolean ChangeDirectory(String Path)
    {
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
    
    public boolean SendFile(String LocalPath, String RemoteName)
    {
        LogWriteLn("");
        try
        {
            sftp.put(LocalPath, RemoteName);
            LogWriteLn("File sended!");
        }
        catch(SftpException e)
        {
            LogWriteLn("Error: " + e.toString());
            return false;
        }
        return true;
    }
    
    public boolean ReceiveFile(String RemoteName, String LocalPath)
    {
        LogWriteLn("");
        try
        {
            sftp.get(RemoteName, LocalPath);
            LogWriteLn("File received!");
        }
        catch(SftpException e)
        {
            LogWriteLn("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void ExecuteAsyncMethod(FlxMethod Meth, String Param1, String Param2) {
        if (Busy) return;
        Busy = true;
        Result = "";

        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    switch ((FlxMethod) params[0]) {
                        case Connect:
                            Connected = Connect();
                            break;
                        case Disconnect:
                            Connected = !Disconnect();
                            break;
                        case GetCurrentLocalPath:
                            Result = GetCurrentLocalPath();
                            break;
                        case GetCurrentRemotePath:
                            Result = GetCurrentRemotePath();
                            break;
                        case ReadDirectoryContent:
                            Result = ReadDirectoryContent();
                            break;
                        case ChangeDirectory:
                            ChangeDirectory((String) params[1]);
                            break;
                        case SendFile:
                            SendFile((String) params[1], (String) params[2]);
                            break;
                        case ReceiveFile:
                            ReceiveFile((String) params[1], (String) params[2]);
                            break;
                    }
                    Busy = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(Meth, Param1, Param2);
        WaitTask();
    }

    private void WaitTask()
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
