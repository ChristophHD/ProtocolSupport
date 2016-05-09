package protocolsupport.protocol.packet.v_1_6;

import net.minecraft.server.v1_9_R1.NetworkManager;
import protocolsupport.protocol.packet.handler.AbstractHandshakeListener;

public class HandshakeListener extends AbstractHandshakeListener {

	public HandshakeListener(NetworkManager networkmanager) {
		super(networkmanager);
	}

	@Override
	public LoginListener getLoginListener(NetworkManager networkManager) {
		return new LoginListener(networkManager);
	}

}
