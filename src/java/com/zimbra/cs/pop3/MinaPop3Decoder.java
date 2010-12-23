/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.pop3;

import java.nio.ByteBuffer;

import com.zimbra.cs.mina.LineBuffer;
import com.zimbra.cs.mina.MinaStats;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class MinaPop3Decoder extends ProtocolDecoderAdapter {
    private final MinaStats stats;
    private final LineBuffer lbuf = new LineBuffer();

    MinaPop3Decoder(MinaStats stats) {
        this.stats = stats;
    }

    @Override
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
        ByteBuffer bb = in.buf();
        while (bb.hasRemaining()) {
            if (lbuf.parse(bb)) {
                out.write(lbuf.toString().trim());
                if (stats != null) {
                    stats.receivedBytes.addAndGet(lbuf.size());
                }
                lbuf.reset();
            }
        }
    }
}
