package com.dm.cube.bin;

import com.sloth.engine.SlothEngine;
import com.sloth.exception.SlothException;

public class PerfMain
{
    public static void main(String[] args)
    {
        SlothEngine se;
        try
        {
            se = new SlothEngine();
            se.start();
        }
        catch (SlothException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
