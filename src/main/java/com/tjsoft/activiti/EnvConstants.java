package com.tjsoft.activiti;

public class EnvConstants {

	public enum ProcessStatus {	
		ENQUEUE("入队",1),COMPLETE("完成",99);
		private String name;
		private int code;
		
		public int getCode() {
			return code;
		}
		public String getName() {
			return name;
		}
		private ProcessStatus(String name,int code)
		{
			this.code = code;
			this.name = name;
		}
		@Override
	    public String toString() {
	        return String.valueOf(this.code);
	    }
		
	}
}
