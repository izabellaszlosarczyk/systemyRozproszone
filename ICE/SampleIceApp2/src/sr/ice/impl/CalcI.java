// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package sr.ice.impl;

import java.io.Serializable;

import Demo.AMD_Calc_add2;
import Demo.RequestCanceledException;
import Demo._CalcDisp;
import Ice.Current;
import sr.ice.server.WorkQueue;

public class CalcI extends _CalcDisp implements Serializable
{
	private int category; 
	private static final long serialVersionUID = -2448962912780867770L;
    private WorkQueue _workQueue;
    private int calcCounter = 0;

    public CalcI(int c)
    {
        _workQueue = null;
        this.category = c;
    }

    
    public CalcI(WorkQueue workQueue, int c)
    {
        _workQueue = workQueue;
        this.category = c;
    }

	
	@Override
	public float add1(float a, float b, Current __current) 
	{
		this.calcCounter = calcCounter + 1;
		int numberOfMethod = 1;
        if (this.category == numberOfMethod){
			System.out.println("Operations counter " + this.calcCounter + " ADD: a = " + a + ", b = " + b + ", result = 0" );
			return 0;
		}
		System.out.println("Operations counter " + this.calcCounter +  " ADD: a = " + a + ", b = " + b + ", result = " + (a+b));
		
		return a + b;
	}

	@Override
	public float subtract(float a, float b, Current __current) 
	{
		this.calcCounter = calcCounter + 1;
		int numberOfMethod = 2;
		if (this.category == numberOfMethod){
			System.out.println("Operations counter " + this.calcCounter + " SUBSTRACT: a = " + a + ", b = " + b + ", result = 0" );
			return 0;
		} 
		System.out.println("Operations counter " + this.calcCounter + " SUBSTRACT: a = " + a + ", b = " + b + ", result = " + (a-b));
	
		return a - b;
	}

	@Override
	public void add2_async(AMD_Calc_add2 __cb, float a, float b, Current __current) throws RequestCanceledException 
	{
		this.calcCounter = calcCounter + 1;
		//int numberOfMethod = 3;
		if(a < 10 && b < 10) //zadanie jest proste
        {
            System.out.println("Operations counter " + this.calcCounter + "ADD (immediate): a = " + a + ", b = " + b + ", result = " + (a+b));
            __cb.ice_response(a+b);
        }
        else //zadanie jest skomplikowane
        {
            _workQueue.addTask(__cb, 5000, a, b);
        }
	}
	
	@Override
	public float multiply(float a, float b, Current __current) 
	{
		this.calcCounter = calcCounter + 1;
		int numberOfMethod = 4;
		if (this.category == numberOfMethod){
			System.out.println("Operations counter " + this.calcCounter + " MULTIPLY: a = " + a + ", b = " + b + ", result = 0" );
			return 0;
		}
		System.out.println("Operations counter " + this.calcCounter + " MULTIPLY: a = " + a + ", b = " + b + ", result = " + (a*b));
		
		return a * b;
	}
	@Override
	public float divide(float a, float b, Current __current) 
	{
		this.calcCounter = calcCounter + 1;
		int numberOfMethod = 5;
		if (this.category == numberOfMethod){
			System.out.println("Operations counter " + this.calcCounter + " DIVIDE: a = " + a + ", b = " + b + ", result = 0" );
			return 0;
		}
		System.out.println("Operations counter " + this.calcCounter + " DIVIDE: a = " + a + ", b = " + b + ", result = " + (a/b));
		
		return a / b;
	}
	
	@Override
	public float modulo(float a, float b, Current __current) 
	{
		this.calcCounter = calcCounter + 1;
		int numberOfMethod = 6;
		if (this.category == numberOfMethod){
			System.out.println("Operations counter " + this.calcCounter + " MODULO: a = " + a + ", b = " + b + ", result = 0" );
			return 0;
		}
		System.out.println("Operations counter " + this.calcCounter + " MODULO: a = " + a + ", b = " + b + ", result = " + (a%b));
		
		return a % b;
	}


	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

}
