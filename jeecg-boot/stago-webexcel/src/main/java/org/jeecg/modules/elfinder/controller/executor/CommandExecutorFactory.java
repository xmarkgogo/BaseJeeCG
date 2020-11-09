package org.jeecg.modules.elfinder.controller.executor;

public interface CommandExecutorFactory
{
	CommandExecutor get(String commandName);
}
