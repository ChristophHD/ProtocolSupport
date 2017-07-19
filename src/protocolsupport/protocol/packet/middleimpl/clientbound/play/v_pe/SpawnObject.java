package protocolsupport.protocol.packet.middleimpl.clientbound.play.v_pe;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.packet.middle.clientbound.play.MiddleSpawnObject;
import protocolsupport.protocol.packet.middleimpl.ClientBoundPacketData;
import protocolsupport.protocol.serializer.ItemStackSerializer;
import protocolsupport.protocol.serializer.MiscSerializer;
import protocolsupport.protocol.serializer.PositionSerializer;
import protocolsupport.protocol.serializer.VarNumberSerializer;
import protocolsupport.protocol.typeremapper.id.IdRemapper;
import protocolsupport.protocol.typeremapper.pe.PEDataValues;
import protocolsupport.protocol.typeremapper.pe.PEPacketIDs;
import protocolsupport.utils.recyclable.RecyclableCollection;
import protocolsupport.utils.recyclable.RecyclableEmptyList;
import protocolsupport.utils.recyclable.RecyclableSingletonList;
import protocolsupport.protocol.utils.types.Position;
import protocolsupport.zplatform.itemstack.ItemStackWrapper;

public class SpawnObject extends MiddleSpawnObject {
	
	@Override
	public RecyclableCollection<ClientBoundPacketData> toData(ProtocolVersion version) {
		switch(entity.getType()) {
			case ITEM: {
				//We need to prepare the item because we can only spawn it after we've received the first metadata update.
				//cache.prepareItem(new PreparedItem(entity.getId(), x, y, z, motX / 8.000F, motY / 8000.F, motZ / 8000.F)); TODO: Add this with metadata update implementation.
				return RecyclableEmptyList.get();
			}
			case ITEM_FRAME: {
				return RecyclableSingletonList.create(createHanging(version, entity.getId(), (int) x, (int) y, (int) z));
			}
			default: {
				return RecyclableSingletonList.create(SpawnLiving.create(
						version,
						entity.getId(), 
						x, y, z,
						motX / 8.000F, motY / 8000.F, motZ / 8000.F, 
						pitch, yaw,
						null, 
						PEDataValues.getObjectEntityTypeId(IdRemapper.ENTITY.getTable(version).getRemap(entity.getType()))
					));
			}
		}
	}
	
	public static ClientBoundPacketData createHanging(ProtocolVersion version,
			int entityId, int x, int y, int z) {
		ClientBoundPacketData serializer = ClientBoundPacketData.create(PEPacketIDs.ADD_HANGING_ENTITY, version);
		VarNumberSerializer.writeSVarLong(serializer, entityId);
		VarNumberSerializer.writeVarLong(serializer, entityId);
		PositionSerializer.writePEPosition(serializer, new Position(x, y, z));
		VarNumberSerializer.writeVarInt(serializer, 0); //?
		return serializer;
	}
	
	public class PreparedItem {
		
		private int entityId;
		private double x; 
		private double y;
		private double z;
		private float motX;
		private float motY;
		private float motZ;
		private ItemStackWrapper itemstack;
		
		PreparedItem(int entityId, double x, double y, double z, float motX, float motY, float motZ) {
			this.entityId = entityId;
			this.x = x;
			this.y = y;
			this.z = z;
			this.motX = motX;
			this.motY = motY;
			this.motZ = motZ;
		}
		
		public int getId() {
			return entityId;
		}
		
		public void setItemStack(ItemStackWrapper itemstack) {
			this.itemstack = itemstack;
		}
		
		public ClientBoundPacketData getSpawnPacket(ProtocolVersion version) {
			ClientBoundPacketData serializer = ClientBoundPacketData.create(PEPacketIDs.ADD_ITEM_ENTITY, version);
			VarNumberSerializer.writeSVarLong(serializer, entityId);
			VarNumberSerializer.writeVarLong(serializer, entityId);
			ItemStackSerializer.writePeSlot(serializer, version, itemstack);
			MiscSerializer.writeLFloat(serializer, (float) x);
			MiscSerializer.writeLFloat(serializer, (float) y);
			MiscSerializer.writeLFloat(serializer, (float) z);
			MiscSerializer.writeLFloat(serializer, motX); 
			MiscSerializer.writeLFloat(serializer, motY);
			MiscSerializer.writeLFloat(serializer, motZ);
			VarNumberSerializer.writeVarInt(serializer, 0);
			return serializer;
		}
	}

}
