package com.turbo.engine.engine;

import com.turbo.engine.exception.ProcessException;
import java.util.Map;

public interface ExpressionCalculator {

    Boolean calculate(String expression, Map<String, Object> dataMap) throws ProcessException;
}
