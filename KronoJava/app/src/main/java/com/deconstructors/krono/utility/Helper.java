package com.deconstructors.krono.utility;

import com.deconstructors.krono.module.Plan;

import java.util.List;

public class Helper
{
    public static Plan getPlan(List<Plan> list, String id)
    {
        for (Plan plan : list)
        {
            if (plan.getPlanID() == id)
            {
                return plan;
            }
        }

        return null;
    }
}
