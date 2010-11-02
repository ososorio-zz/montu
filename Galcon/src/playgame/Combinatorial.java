package playgame;

import java.util.Collection;
import java.util.Stack;

public abstract class Combinatorial {
	int limit;
	int combSize;
	Stack<Integer> s = new Stack<Integer>();

	public Combinatorial(int combinationSize, int setSize) {
		limit = setSize;
		combSize = combinationSize;
	}
	
	public void setCombinationSize(int combinationSize)
	{
		combSize = combinationSize;
	}

	public void setSetSize(int setSize)
	{
		limit = setSize;
	}
	
	public void start()
	{
		combinations(0,combSize);
	}


	final void combinations(int from, int curDepth) {
		if (curDepth == 0) // /|| (depth == curDepth && from == limit - 1 ))
		{
			onCombination(s);
			s.pop();
			return;
		}

		for (int i = from; i < limit; i++) {
			s.push(i);
			combinations(i + 1, curDepth -1);
		}
		if (!s.isEmpty())
			s.pop();
	}

	abstract public void onCombination(Collection<Integer> comb);

	public static void main(String[] args) {
		Combinatorial comb = new Combinatorial(5,23) {
			int cnt = 0;
			long time = System.currentTimeMillis();
			
			public void start()
			{
				super.start();
				System.out.println(cnt);
				System.out.println(System.currentTimeMillis() - time);
			}
			
			@Override
			public void onCombination(Collection<Integer> comb) {
//				System.out.println(comb);
				cnt++;
			}
		};
		for(int i = 1; i < 6; i++)
		{
			comb.setCombinationSize(i);
			comb.start();
			System.out.println("---");
		}
	}
}
