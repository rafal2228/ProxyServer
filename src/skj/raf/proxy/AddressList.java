package skj.raf.proxy;

import java.util.ArrayList;

public class AddressList {
	
	public enum ListStatus {
		ALLOW(),
		DENY(),
		DOESNT_HAVE();
	}
	
	private ArrayList<ClientList> _allow;
	private ArrayList<ClientList> _deny;
	private ArrayList<String> _addresses; // Host addresses
	public final String name;
	
	public AddressList(String name) {
		this.name = name;
		_allow = new ArrayList<>();
		_allow.add(new ClientList(name + ".allow"));
		
		_deny = new ArrayList<>();
		_deny.add(new ClientList(name + ".deny"));
		
		_addresses = new ArrayList<>();
	}
	
	public void denyClient(String client) {
		if(_allow.get(0).match(client)) _allow.get(0).remove(client);
		_deny.get(0).add(client);
	}
	
	public void denyList(ClientList list) {
		if(_allow.contains(list)) _allow.remove(list);
		_deny.add(list);
	}
	
	public void allowClient(String client) {
		if(_deny.get(0).match(client)) _deny.get(0).remove(client);
		_allow.get(0).add(client);
	}
	
	public void allowList(ClientList list) {
		if(_deny.contains(list)) _deny.remove(list);
		_allow.add(list);
	}
	
	public void addAddress(String addr) {
		_addresses.add(addr);
	}
	
	public ListStatus canConnectTo(String client, String address) {
		boolean contains = _addresses.stream().anyMatch(e -> e.contains(address));
		
		if(contains) {
			if(_allow.stream().anyMatch(e -> e.contains(client))) return ListStatus.ALLOW;
			else if(_deny.stream().anyMatch(e -> e.contains(client))) return ListStatus.DENY;
		}
		
		return ListStatus.DOESNT_HAVE;
	}
}