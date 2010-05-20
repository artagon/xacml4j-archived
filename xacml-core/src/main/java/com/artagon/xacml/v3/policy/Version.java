package com.artagon.xacml.v3.policy;


import com.artagon.xacml.v3.XacmlObject;
import com.google.common.base.Preconditions;


public class Version extends XacmlObject implements Comparable<Version> 
{
	private  static final String VERSION_PATTERN = "(\\d+\\.)*\\d+";
	
	private String value;
    private int[] version;

    /**
     * Constructs version from
     * a given string
     * 
     * @param v a version represented
     * as string
     */
    private Version(String version) 
    {
    	Preconditions.checkNotNull(version);
    	this.value = version;
    	this.version = parseVersion(version);
    }
    
    /**
     * Gets version value 
     * 
     * @return version value as 
     * a string
     */
    public String getValue() {
       return value;
    }

    @Override
    public boolean equals(Object other) {
    	if (other == null) {
    		return false;
    	}
    	if (this == other) {
    		return true;
    	}
    	if (!(other instanceof Version)) {
    		return false;
    	}
    	return compareTo((Version)other) == 0;
    }
   
    @Override
    public int compareTo(Version v) 
    {
        int min = Math.min(version.length, v.version.length);
        for(int i = 0; i < min; i++){
        	int r = version[i] - v.version[i];
        	if(r != 0){
        		return r > 0?1:-1;
        	}
        }
        if (checkAllZeros(version, min) && checkAllZeros(v.version, min)) {
        	return 0;
        }
        return version.length - v.version.length;
    }
    
    private boolean checkAllZeros(int[] versions, int startIdx)
    {
		for (int i = versions.length-1; i >= startIdx; i--) {
			if (versions[i] != 0) return false;
		}
		return true;
	}

    /**
     * Parses given version string and returns
     * version as an array of non-negative integers
     * 
     * @param version a version string
     * @return an array of non-negative integers
     */
	private static int[] parseVersion(String version)
    {
    	Preconditions.checkArgument(version.matches(VERSION_PATTERN));
    	 String[] vc = version.split("\\.");
    	 int[] v = new int[vc.length];
    	 for(int i = 0; i < vc.length; i++){
    		 v[i] = Integer.parseInt(vc[i]);
    		 if(v[i] < 0){
    			 throw new IllegalArgumentException(Integer.toString(v[i]));
    		 }
    	 }
    	 return v;
    }
	
	/**
	 * A static factory method to 
	 * create {@link Version} instances 
	 * from a given string
	 * 
	 * @param version a version
	 * @return {@link Version} instance
	 */
    public static Version valueOf(String version) {
        return new Version(version);
    }
    
    /**
	 * A static factory method to 
	 * create {@link Version} instances 
	 * from a given integer
	 * 
	 * @param version a version
	 * @return {@link Version} instance
	 */
    public static Version valueOf(int version)
    {
    	Preconditions.checkArgument(version > 0);
    	return valueOf(Integer.toString(version));
    }
}