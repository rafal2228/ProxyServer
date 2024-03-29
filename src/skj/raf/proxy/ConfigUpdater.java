package skj.raf.proxy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ConfigUpdater implements Runnable{

	private static final int BUFFER_SIZE = 32784;
	
	private DatagramPacket _packet;
	private DatagramSocket _server;
	private byte[] _buffer;
	
	public ConfigUpdater(int port) throws SocketException {
		_buffer = new byte[BUFFER_SIZE]; 
		_packet = new DatagramPacket(_buffer, _buffer.length);
		_server = new DatagramSocket(port);
		System.out.println("Starting config updater at " + _server.getLocalAddress().getHostAddress() + ":" + port);
	}
	
	public boolean setHash(String uuid) {
		_packet.setData(("HASH;" + uuid).getBytes());
		try {
			_server.send(_packet);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return true;
	}
	
	public void sendList(String list) {
		_packet.setData(("GET;" + list).getBytes());
		try {
			_server.send(_packet);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void sendRemoval(String removed) {
		_packet.setData(("REMOVE;" + removed).getBytes());
		try {
			_server.send(_packet);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void sendSave(String save) {
		_packet.setData(("REMOVE;" + save).getBytes());
		try {
			_server.send(_packet);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void run() {
		String command;
		while(true) {
			try {
				_packet.setData(_buffer);
				_server.receive(_packet);
				command = new String(_packet.getData(), 0, _packet.getLength());

				try {
					ConfigParser.parseConfigurator(command, this);
					_packet.setData("DONE".getBytes());
				} catch (Exception e) {
					if(e.getMessage() != null) {
						_packet.setData(e.getMessage().getBytes());
					} else {
						byte[] response = "ERROR".getBytes();
						_packet.setData(response);
					}
				} finally {
					_server.send(_packet);
				}
				
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
}
