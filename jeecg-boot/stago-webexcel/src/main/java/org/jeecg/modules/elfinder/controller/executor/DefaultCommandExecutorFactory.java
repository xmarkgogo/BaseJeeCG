package org.jeecg.modules.elfinder.controller.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DefaultCommandExecutorFactory implements CommandExecutorFactory
{
	String _classNamePattern;

	private Map<String, CommandExecutor> _map = new HashMap<String, CommandExecutor>();

	private CommandExecutor _fallbackCommand;
	private Logger logger = LoggerFactory.getLogger(DefaultCommandExecutorFactory.class);
	@Override
	public CommandExecutor get(String commandName)
	{
		if (_map.containsKey(commandName))
			return _map.get(commandName);

		try
		{
             //cn.bluejoe.elfinder.controller.executors.OpenCommandExecutor
			// org.grapheco.elfinder.controller.executors
//			logger.debug("执行命令："+commandName);
			String className = _classNamePattern.replaceAll("%s", commandName.substring(0, 1).toUpperCase() + commandName.substring(1));
//			logger.debug("className："+className);
			return (CommandExecutor) Class.forName(className).newInstance();
		}
		catch (Exception e)
		{
			// not found
			e.printStackTrace();
			return _fallbackCommand;
		}
	}

	public String getClassNamePattern()
	{
		return _classNamePattern;
	}

	public Map<String, CommandExecutor> getMap()
	{
		return _map;
	}

	public CommandExecutor getFallbackCommand()
	{
		return _fallbackCommand;
	}

	public void setClassNamePattern(String classNamePattern)
	{
		_classNamePattern = classNamePattern;
	}

	public void setMap(Map<String, CommandExecutor> map)
	{
		_map = map;
	}

	public void setFallbackCommand(CommandExecutor fallbackCommand)
	{
		this._fallbackCommand = fallbackCommand;
	}
}
