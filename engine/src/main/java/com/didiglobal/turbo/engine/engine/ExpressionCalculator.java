package com.didiglobal.turbo.engine.engine;

import com.didiglobal.turbo.engine.exception.ProcessException;
import java.util.Map;

public interface ExpressionCalculator {

    Boolean calculate(String expression, Map<String, Object> dataMap) throws ProcessException;
}
