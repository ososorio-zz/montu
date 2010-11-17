 /*
 *
 * Copyright (c) 2008-2010 Aha Mobile, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Aha Mobile, Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered
 * into with Aha Mobile.
 *
 * AHA MOBILE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. AHA MOBILE SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
 package playgame;

import java.util.Arrays;
import java.util.Random;

/**
 * Linear reinforcement learning automata.
 * After performing an action, the automata performance
 * should be evaluated and then he should be rewarded or penalized.
 * 
 * The use pattern is :
 * 
 * LearningAutomata autom = new LearningAutomata(..);
 * 
 * action = autom.nextAction();
 * //perform the action
 * if( improved )
 *    autom.reward();
 * else
 *    autom.penalize();
 *    
 * @author Edwin Navarrete
 *
 */
public class LearningAutomata
{
	private int[] action;
	private double reward;
	private int sum = 0;
	private double rewardCmpl;
	private double penaltyCmpl;
	private double penaltyGap;
	private double rewardGap;
	private double penalty;

	/**
	 * Constructor
	 * @param reward it must be positive < 1 
	 * @param penalty it must be positive < 1 
	 * @param actionCnt it must be > 1 
	 */
	public LearningAutomata(double reward, double penalty, int[] actions)
	{
		this.reward = reward;
		this.action = actions;
		this.rewardCmpl = 1 - reward;
		this.penalty = penalty;
		this.penaltyCmpl = 1 - penalty;
		this.penaltyGap = penalty * sum / (action.length - 1);
		this.rewardGap = reward * sum / (action.length - 1);
		for (int i = 0; i < actions.length; i++) 
			sum += actions[i];
	}
	
	/**
	 * Reward an action
	 */
	public void reinforce(int curAction, double env)
	{
		int rest = sum;
		double envCompl = 1 - env;
		for (int i = 0; i < action.length; i++)
		{
			if (i != curAction)
			{
				action[i] += (int) Math.round(
						env * (rewardGap - reward * action[i]) - action[i] * reward * envCompl);
			}
			else
			{
				double change =  reward * (sum - action[i]) * envCompl - reward * action[i] * env;
				int intChange = (int) Math.round(change);
				//Force a minimal change of 1 
				if(intChange == 0)
				{
					intChange = (env < 0.5D ? 1 : -1);
				}
				action[i] += intChange;
			}
			rest -= action[i];
		}
		if(env <= 0.5D)
		{
			action[curAction] += rest;
		}
		else
		{
			while(rest > 0)
			{
				for (int i = 0; i < action.length && rest > 0; i++)
				{
					if (i != curAction)
					{
						action[i] ++;
						rest--;
					}
				}
			}
		}
	}

	/**
	 * Reward an action with environment score
	 */
	public void reward(int curAction, double env)
	{
		int rest = sum;
		for (int i = 0; i < action.length; i++)
		{
			if (i != curAction)
				action[i] = (int) Math.round(action[i] * (1 - reward * env));
			else
				action[i] = (int) Math.round(action[i] + env * reward * (sum - action[i]));
			rest -= action[i];
		}
		action[curAction] += rest;
	}

	/**
	 * Penalize an action
	 */
	public void penalize(int curAction, double env)
	{
		int rest = sum;
		for (int i = 0; i < action.length; i++)
		{
			if (i != curAction)
				action[i] = (int) Math.round( (1 - env * penalty) * action[i] + penaltyGap);
			else
				action[i] = (int) Math.max(1,Math.floor( (1 - env * penalty) * action[i]));
			rest -= action[i];
		}
		while(rest > 0)
		{
			for (int i = 0; i < action.length && rest > 0; i++)
			{
				if (i != curAction)
				{
					action[i] ++;
					rest--;
				}
			}
		}
	}
	
	
	/**
	 * Reward an action
	 */
	public void reward(int curAction)
	{
		int rest = sum;
		for (int i = 0; i < action.length; i++)
		{
			if (i != curAction)
				action[i] = (int) Math.round(action[i] * rewardCmpl);
			else
				action[i] = (int) Math.round(action[i] + reward * (sum - action[i]));
			rest -= action[i];
		}
		action[curAction] += rest;
	}

	/**
	 * Penalize an action
	 */
	public void penalize(int curAction)
	{
		int rest = sum;
		for (int i = 0; i < action.length; i++)
		{
			if (i != curAction)
				action[i] = (int) Math.round(penaltyCmpl * action[i] + penaltyGap);
			else
				action[i] = (int) Math.round(penaltyCmpl * action[i]);
			rest -= action[i];
		}
		while(rest > 0)
		{
			for (int i = 0; i < action.length && rest > 0; i++)
			{
				if (i != curAction)
				{
					action[i] ++;
					rest--;
				}
			}
		}
	}
	
	public String toString()
	{
		return Arrays.toString(action);
	}

	public int getDiff()
	{
		int localSum = 0;
		for (int i = 0; i < action.length; i++) {
			localSum += action[i];
		}
		return localSum - sum;
	}
	
	public static void main(String[] args) {
		int[] actions = new int[] { 50, 7, 36, 12, 34, 24, 17, 87, 33, 67, 90,
				12, 77, 98, 13, 20 };
		LearningAutomata aut = new LearningAutomata(Math.sqrt(3)/17, Math.sqrt(13)/13, actions );
		for (int i = 0; i < actions.length; i++) {
//			aut.reward(i);
			aut.reward(2);
			if(aut.getDiff() != 0)
				throw new IllegalStateException("Difference:"+aut.getDiff());
		}
		System.out.println(aut);
	}
}