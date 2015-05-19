package br.DroidLib;

public class Timer implements Runnable
{

	private int secsToWait;
	private Thread waitTimer;
	private Thread callback;

	
	
	
	/**
	 * @param callback
	 *            the callback to set
	 */
	public void setCallback(Thread callback)
	{
		this.callback = callback;
	}

	/**
	 * @return the callback
	 */
	public Thread getCallback()
	{
		return callback;
	}

	/**
	 * @param secsToWait
	 *            the secsToWait to set
	 */
	public void setSecsToWait(int secsToWait)
	{
		this.secsToWait = secsToWait;
	}

	/**
	 * @return the secsToWait
	 */
	public int getSecsToWait()
	{
		return secsToWait;
	}

	public void start()
	{
		waitTimer=new Thread(this,"timer ticker");
		waitTimer.start();
	}



	
	@Override
	public void run()
	{
		try
		{
			Thread.sleep(this.secsToWait);
		} catch (final InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (callback!=null)
			callback.start();		
	}
}
