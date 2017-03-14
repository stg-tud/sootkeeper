package de.tud.cs.peaks.testcases;

import java.lang.reflect.Field;

public class SimpleReflection  {
	
	private static int x = 0;
	
	public static Field getAssetField(Class cls, String name){
		
		Field asset = null;
		
		try{
			asset = cls.getDeclaredField(name);
		} catch(NoSuchFieldException e){
			e.printStackTrace();
		}
		if (!asset.isAccessible())
			asset.setAccessible(true);
		
		return asset;
	}
}
