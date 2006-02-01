/*
 * OccurenceRecord.java
 *
 * Created on 23. leden 2006, 21:46
 *
 */

package net.sf.plantlore.client.dblayer;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Jakub
 */
public class OccurenceRecord
{
    private int id;
    private String recordDbId;
    private String recordId;
    private int year;
    private int month;
    private int day;
    private Date time;
    //private Date isoDateTime;
    //private String DateSource;
    private String herbarium;
    private Date createdWhen;
    private UserRecord createdWho; //?
    private Date updatedWhen;
    private UserRecord updatedWho; //?
    private String note;
    
    /** we must not forget create better setters
     *       - or at least think about it more
     *
     */
    private AuthorRecord[] authorRecords; //?
    
    
    
    /** Creates a new instance of OccurenceRecord */
    public OccurenceRecord()
    {
    }

    public String getRecordDbId()
    {
        return recordDbId;
    }

    public void setRecordDbId(String recordDbId)
    {
        this.recordDbId = recordDbId;
    }

    public String getRecordId()
    {
        return recordId;
    }

    public void setRecordId(String recordId)
    {
        this.recordId = recordId;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public int getMonth()
    {
        return month;
    }

    public void setMonth(int month)
    {
        this.month = month;
    }

    public int getDay()
    {
        return day;
    }

    public void setDay(int day)
    {
        this.day = day;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public String getHerbarium()
    {
        return herbarium;
    }

    public void setHerbarium(String herbarium)
    {
        this.herbarium = herbarium;
    }

    public Date getCreatedWhen()
    {
        return createdWhen;
    }

    public void setCreatedWhen(Date createdWhen)
    {
        this.createdWhen = createdWhen;
    }

    public UserRecord getCreatedWho()
    {
        return createdWho;
    }

    public void setCreatedWho(UserRecord createdWho)
    {
        this.createdWho = createdWho;
    }

    public Date getUpdatedWhen()
    {
        return updatedWhen;
    }

    public void setUpdatedWhen(Date updatedWhen)
    {
        this.updatedWhen = updatedWhen;
    }

    public UserRecord getUpdatedWho()
    {
        return updatedWho;
    }

    public void setUpdatedWho(UserRecord updatedWho)
    {
        this.updatedWho = updatedWho;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public AuthorRecord[] getAuthorRecords()
    {
        return authorRecords;
    }

    public void setAuthorRecords(AuthorRecord[] authorRecords)
    {
        this.authorRecords = authorRecords;
    }

    
}
