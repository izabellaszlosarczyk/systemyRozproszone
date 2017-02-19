// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package sr.ice.client;

import java.util.Random;

import Demo.*;
import Ice.AsyncResult;

public class Client4
{
	public static void main(String[] args) 
	{
		int status = 0;
		Ice.Communicator communicator = null;
		int i = 0;
		String type = " ";
		try {
			
			do
			{
				communicator = Ice.Util.initialize(args);
				String p = "K4/c4"+ i;
				Ice.ObjectPrx base1 = communicator.stringToProxy(p + ":tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");
				CalcPrx calc1 = CalcPrxHelper.checkedCast(base1);
				if (calc1 == null) throw new Error("Invalid proxy1");
				
				Random random = new Random();
				float r = calc1.add1(7, 8);
				System.out.println("Category 4, RESULT add1 = " + r);
				r = calc1.divide(7, 8);
				System.out.println("Category 4, RESULT divide = " + r);
				r = calc1.multiply(7, 8);
				System.out.println("Category 4, RESULT multiply = " + r);
				r = calc1.modulo(7, 8);
				System.out.println("Category 4, RESULT modulo = " + r);
				r = calc1.subtract(7, 8);
				System.out.println("Category 4, RESULT subtract = " + r);
				
				i = i + 1;
			}
			while (i < 30);


		} catch (Ice.LocalException e) {
			e.printStackTrace();
			status = 1;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			status = 1;
		}
		if (communicator != null) {
			// Clean up
			//
			try {
				communicator.destroy();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				status = 1;
			}
		}
		System.exit(status);
	}

}