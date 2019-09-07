package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class DemoApplicationTests {

    public static void main(String[] args) {
        DemoApplicationTests tests = new DemoApplicationTests();
        int[] nums = {0, 1, 2};
        threeSumClosest(nums, 0);
    }

    public static int threeSumClosest(int[] nums, int target) {
        if (nums == null || nums.length < 3) return 0;
        Arrays.sort(nums);
        int min = Integer.MAX_VALUE;
        int result = Integer.MAX_VALUE;
        int len = nums.length;
        for (int i = 0; i < len - 2; i++) {
            if (min == 0) break;
            int L = i + 1;
            int R = len - 1;
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            if (nums[i] > target) break;
            while (L < R) {
                int tmp = nums[i] + nums[L] + nums[R];
                if (tmp == target) {
                    min = 0;
                    result = tmp;
                    while (L < R && nums[L] == nums[L + 1]) L++;
                    while (L < R && nums[R - 1] == nums[R]) R--;
                    L++;
                    R--;
                    break;
                }
                else if (tmp < target ) {
                    if (Math.abs(target - tmp) < min) {
                        min = Math.abs(target - tmp);
                        result = tmp;
                    }
                    L++;
                } else {
                    if (Math.abs(tmp - target) < min ) {
                        min = Math.abs(tmp - target);
                        result = tmp;
                    }

                    R--;
                }
            }
        }
        return result;
    }
}
