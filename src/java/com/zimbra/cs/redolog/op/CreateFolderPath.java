/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/*
 * Created on 2004. 12. 13.
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

/**
 * @author jhahm
 */
public class CreateFolderPath extends RedoableOp {

    private String mPath;
    private byte mAttrs;
    private byte mDefaultView;
    private int mFlags;
    private byte mColor;
    private String mUrl;
    private int mFolderIds[];

    public CreateFolderPath() { }

    public CreateFolderPath(int mailboxId, String name, byte attrs, byte view, int flags, byte color, String url) {
        setMailboxId(mailboxId);
        mPath = name == null ? "" : name;
        mAttrs = attrs;
        mDefaultView = view;
        mFlags = flags;
        mColor = color;
        mUrl = url == null ? "" : url;
    }

    public int[] getFolderIds() {
        return mFolderIds;
    }

    public void setFolderIds(int parentIds[]) {
        mFolderIds = parentIds;
    }

    public int getOpCode() {
        return OP_CREATE_FOLDER_PATH;
    }

    protected String getPrintableData() {
        StringBuilder sb = new StringBuilder("name=").append(mPath);
        sb.append(", attrs=").append(mAttrs).append(", view=").append(mDefaultView);
        sb.append(", flags=").append(mFlags).append(", color=").append(mColor);
        sb.append(", url=").append(mUrl);
        if (mFolderIds != null) {
            sb.append(", folderIds=[");
            for (int i = 0; i < mFolderIds.length; i++) {
                sb.append(mFolderIds[i]);
                if (i < mFolderIds.length - 1)
                    sb.append(", ");
            }
            sb.append("]");
        }
        return sb.toString();
    }

    protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeUTF(mPath);
        out.writeByte(mAttrs);
        out.writeByte(mDefaultView);
        out.writeInt(mFlags);
        out.writeByte(mColor);
        out.writeUTF(mUrl);
        if (mFolderIds != null) {
            out.writeInt(mFolderIds.length);
            for (int i = 0; i < mFolderIds.length; i++)
                out.writeInt(mFolderIds[i]);
        } else
            out.writeInt(0);
    }

    protected void deserializeData(RedoLogInput in) throws IOException {
        mPath = in.readUTF();
        mAttrs = in.readByte();
        mDefaultView = in.readByte();
        mFlags = in.readInt();
        mColor = in.readByte();
        mUrl = in.readUTF();
        int numParentIds = in.readInt();
        if (numParentIds > 0) {
            mFolderIds = new int[numParentIds];
            for (int i = 0; i < numParentIds; i++)
                mFolderIds[i] = in.readInt();
        }
    }

    public void redo() throws Exception {
        int mboxId = getMailboxId();
        Mailbox mailbox = MailboxManager.getInstance().getMailboxById(mboxId);
        try {
            mailbox.createFolder(getOperationContext(), mPath, mAttrs, mDefaultView, mFlags, mColor, mUrl);
        } catch (MailServiceException e) {
            String code = e.getCode();
            if (code.equals(MailServiceException.ALREADY_EXISTS)) {
                if (mLog.isInfoEnabled())
                    mLog.info("Folder " + mPath + " already exists in mailbox " + mboxId);
            } else {
                throw e;
            }
        }
    }
}
