/**
    QuasselDroid - Quassel client for Android
 	Copyright (C) 2011 Martin Sandsmark <martin.sandsmark@kde.org>

    This program is free software: you can redistribute it and/or modify it
    under the terms of the GNU General Public License as published by the Free
    Software Foundation, either version 3 of the License, or (at your option)
    any later version, or under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either version 2.1 of
    the License, or (at your option) any later version.

 	This program is distributed in the hope that it will be useful,
 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 	GNU General Public License for more details.

    You should have received a copy of the GNU General Public License and the
    GNU Lesser General Public License along with this program.  If not, see
    <http://www.gnu.org/licenses/>.
 */

package com.lekebilen.quasseldroid.qtcomm.serializers.quassel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import android.text.Spannable;
import android.text.SpannableString;

import com.lekebilen.quasseldroid.BufferInfo;
import com.lekebilen.quasseldroid.IrcMessage;
import com.lekebilen.quasseldroid.qtcomm.DataStreamVersion;
import com.lekebilen.quasseldroid.qtcomm.QDataInputStream;
import com.lekebilen.quasseldroid.qtcomm.QDataOutputStream;
import com.lekebilen.quasseldroid.qtcomm.QMetaTypeRegistry;
import com.lekebilen.quasseldroid.qtcomm.QMetaTypeSerializer;

public class MessageSerializer implements QMetaTypeSerializer<IrcMessage> {

	@Override
	public void serialize(QDataOutputStream stream, IrcMessage data,
			DataStreamVersion version) throws IOException {
		stream.writeInt(data.messageId);
		stream.writeUInt(data.timestamp.getTime() / 1000, 32);
		stream.writeUInt(data.type.getValue(), 32);
		stream.writeByte(data.flags);
		stream.writeBytes(data.sender);// TODO FIXME
		stream.writeBytes(data.content.toString());//ditto
	}

	@Override
	public IrcMessage unserialize(QDataInputStream stream,
			DataStreamVersion version) throws IOException {
		IrcMessage ret = new IrcMessage();
		ret.messageId = stream.readInt();
		ret.timestamp = new Date(stream.readUInt(32) * 1000);
		ret.type = IrcMessage.Type.getForValue((int) stream.readUInt(32));
		ret.flags = stream.readByte();
		ret.bufferInfo = (BufferInfo) QMetaTypeRegistry.instance().getTypeForName("BufferInfo").getSerializer().unserialize(stream, version);
		ret.sender = (String) QMetaTypeRegistry.instance().getTypeForName("QByteArray").getSerializer().unserialize(stream, version);
		ret.content =  SpannableString.valueOf((String)QMetaTypeRegistry.instance().getTypeForName("QByteArray").getSerializer().unserialize(stream, version));
		
		return ret;
	}

}
