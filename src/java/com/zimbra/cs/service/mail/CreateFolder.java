/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */

/*
 * Created on Aug 27, 2004
 */
package com.zimbra.cs.service.mail;

import java.util.Map;

import com.zimbra.cs.mailbox.ACL;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.operation.CreateFolderOperation;
import com.zimbra.cs.operation.Operation.Requester;
import com.zimbra.cs.service.ServiceException;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.session.Session;
import com.zimbra.cs.util.ZimbraLog;
import com.zimbra.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * @author dkarp
 */
public class CreateFolder extends MailDocumentHandler {

    private static final String[] TARGET_FOLDER_PATH = new String[] { MailService.E_FOLDER, MailService.A_FOLDER };
    private static final String[] RESPONSE_ITEM_PATH = new String[] { };
    protected String[] getProxiedIdPath(Element request)     { return TARGET_FOLDER_PATH; }
    protected boolean checkMountpointProxy(Element request)  { return true; }
    protected String[] getResponseItemPath()  { return RESPONSE_ITEM_PATH; }

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext lc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(lc);
        Mailbox.OperationContext octxt = lc.getOperationContext();
        Session session = getSession(context);

        Element t = request.getElement(MailService.E_FOLDER);
        String name      = t.getAttribute(MailService.A_NAME);
        String view      = t.getAttribute(MailService.A_DEFAULT_VIEW, null);
        String flags     = t.getAttribute(MailService.A_FLAGS, null);
        byte color       = (byte) t.getAttributeLong(MailService.A_COLOR, MailItem.DEFAULT_COLOR);
        String url       = t.getAttribute(MailService.A_URL, null);
        ItemId iidParent = new ItemId(t.getAttribute(MailService.A_FOLDER), lc);
        boolean fetchIfExists = t.getAttributeBool(MailService.A_FETCH_IF_EXISTS, false);
        ACL acl          = FolderAction.parseACL(t.getOptionalElement(MailService.E_ACL));

        CreateFolderOperation op = new CreateFolderOperation(session, octxt, mbox, Requester.SOAP, name, iidParent, view, flags, color, url, fetchIfExists);
        op.schedule();
        Folder folder = op.getFolder();

        // set the folder ACL as a separate operation, when appropriate
        if (acl != null && !op.alreadyExisted()) {
            try {
                mbox.setPermissions(octxt, folder.getId(), acl);
            } catch (ServiceException e) {
                try {
                    // roll back folder creation
                    mbox.delete(null, folder.getId(), MailItem.TYPE_FOLDER);
                } catch (ServiceException nse) {
                    ZimbraLog.soap.warn("error ignored while rolling back folder create", nse);
                }
                throw e;
            }
        }

        Element response = lc.createElement(MailService.CREATE_FOLDER_RESPONSE);
        if (folder != null)
            ToXML.encodeFolder(response, lc, folder);
        return response;
    }
}
