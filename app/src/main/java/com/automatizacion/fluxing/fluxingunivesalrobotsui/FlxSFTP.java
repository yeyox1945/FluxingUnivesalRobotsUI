/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import com.jcraft.jsch.*;
import java.util.Vector;

/**
 *
 * @author Chava
 */
public class FlxSFTP {
    private final JSch jsch = new JSch();
    private Session session;
    private ChannelSftp sftp;
    public String FileName = "void.txt";
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
            Vector filelist = sftp.ls(GetCurrentRemotePath());
            int Count = 0;
            for(int i=0; i<filelist.size();i++)
            {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) filelist.get(i);
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
        return ret;
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
            LogWriteLn("Error: " + e.getMessage());
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
