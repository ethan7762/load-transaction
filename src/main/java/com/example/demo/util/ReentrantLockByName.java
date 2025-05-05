package com.example.demo.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockByName {

	ConcurrentHashMap<String, ReentrantLock> mapStringLock;
	
	public ReentrantLockByName(){
		mapStringLock = new ConcurrentHashMap<String, ReentrantLock>();
	}
	
	public ReentrantLockByName(ConcurrentHashMap<String, ReentrantLock> mapStringLock){
		this.mapStringLock = mapStringLock;
	}

	public ReentrantLock getLock(String key) {
		
		if(key == null || key.equals("") ) {
			return null;
		}
		
		ReentrantLock initValue = createIntanceLock();
		ReentrantLock lock = mapStringLock.putIfAbsent(key, initValue);
		if (lock == null) {
			lock = initValue;
		}
		return lock;
	}

	protected ReentrantLock createIntanceLock() {
		return new ReentrantLock();
	}
	
}