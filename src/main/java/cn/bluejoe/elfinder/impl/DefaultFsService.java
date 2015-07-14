package cn.bluejoe.elfinder.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import cn.bluejoe.elfinder.service.FsItem;
import cn.bluejoe.elfinder.service.FsSecurityChecker;
import cn.bluejoe.elfinder.service.FsService;
import cn.bluejoe.elfinder.service.FsServiceConfig;
import cn.bluejoe.elfinder.service.FsVolume;

public class DefaultFsService implements FsService
{
	FsSecurityChecker _securityChecker;

	FsServiceConfig _serviceConfig;

	public FsServiceConfig getServiceConfig()
	{
		return _serviceConfig;
	}

	public void setServiceConfig(FsServiceConfig serviceConfig)
	{
		_serviceConfig = serviceConfig;
	}

	Map<FsVolume, String> _volumeIds = new HashMap<FsVolume, String>();

	FsVolume[] _volumes;

	String[][] escapes = { { "+", "_P" }, { "-", "_M" }, { "/", "_S" }, { ".", "_D" }, { "=", "_E" } };

	@Override
	public FsItem fromHash(String hash)
	{
		for (FsVolume v : _volumes)
		{
			String prefix = getVolumeId(v) + "_";

			if (hash.equals(prefix))
			{
				return v.getRoot();
			}

			if (hash.startsWith(prefix))
			{
				String localHash = hash.substring(prefix.length());

				for (String[] pair : escapes)
				{
					localHash = localHash.replace(pair[1], pair[0]);
				}

				String relativePath = new String(Base64.decodeBase64(localHash));
				return v.fromPath(relativePath);
			}
		}

		return null;
	}

	@Override
	public String getHash(FsItem item) throws IOException
	{
		String relativePath = item.getVolume().getPath(item);
		String base = new String(Base64.encodeBase64(relativePath.getBytes()));

		for (String[] pair : escapes)
		{
			base = base.replace(pair[0], pair[1]);
		}

		return getVolumeId(item.getVolume()) + "_" + base;
	}

	public FsSecurityChecker getSecurityChecker()
	{
		return _securityChecker;
	}

	@Override
	public String getVolumeId(FsVolume volume)
	{
		return _volumeIds.get(volume);
	}

	public FsVolume[] getVolumes()
	{
		return _volumes;
	}

	public void setSecurityChecker(FsSecurityChecker securityChecker)
	{
		_securityChecker = securityChecker;
	}

	public void setVolumes(FsVolume[] volumes)
	{
		_volumes = volumes;
		char vid = 'A';
		for (FsVolume volume : volumes)
		{
			_volumeIds.put(volume, "" + vid);
			vid++;
		}
	}
}
