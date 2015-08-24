package protocolsupport.protocol.transformer.v_1_6.utils;

import gnu.trove.map.TIntObjectMap;

import java.io.IOException;
import java.util.Iterator;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.watchedentity.types.WatchedEntity;
import protocolsupport.utils.DataWatcherObject;
import protocolsupport.utils.DataWatcherObject.ValueType;
import protocolsupport.utils.DataWatcherSerializer;
import protocolsupport.utils.Utils;

public class DataWatcherFilter {

	public static byte[] filterEntityData(ProtocolVersion from, ProtocolVersion to, WatchedEntity entity, byte[] data) throws IOException {
		TIntObjectMap<DataWatcherObject> objects = DataWatcherSerializer.decodeData(from, data);
		if (entity == null) {
			return DataWatcherSerializer.encodeData(to, objects);
		}
		if (entity.isLiving()) {
			DataWatcherObject nametag = objects.remove(2);
			if (nametag != null) {
				nametag.value = Utils.clampString((String) nametag.value, 64);
				objects.put(10, nametag);
			}
			DataWatcherObject nametagvisible = objects.remove(3);
			if (nametagvisible != null) {
				objects.put(11, nametagvisible);
			}
		}
		if (entity.isAgeable()) {
			DataWatcherObject object = objects.get(12);
			if (object != null) {
				object.toInt();
			}
		} else if (entity.isEnderman()) {
			DataWatcherObject object = objects.get(16);
			if (object != null) {
				object.toByte();
			}
		} else if (entity.isMinecart()) {
			DataWatcherObject object = objects.get(20);
			if (object != null) {
				int value = (int) object.value;
				int p1 = value & 0xFFFF;
				int p2 = value >> 12;
				object.value = (p2 << 16) | p1;
			}
		} else if (entity.isItemFrame()) {
			if (objects.containsKey(8)) {
				objects.put(2, objects.get(8));
			}
			if (objects.containsKey(9)) {
				int rotation = (byte) objects.get(9).value;
				objects.put(3, new DataWatcherObject(ValueType.BYTE, ((byte) (rotation >> 1))));
			}
		}
		// remove type 7 watched objects
		Iterator<DataWatcherObject> iterator = objects.valueCollection().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().type == ValueType.VECTOR3F) {
				iterator.remove();
			}
		}
		// add object in case objects list is empty
		if (objects.isEmpty()) {
			objects.put(31, new DataWatcherObject(ValueType.BYTE, (byte) 0));
		}
		return DataWatcherSerializer.encodeData(to, objects);
	}

}
