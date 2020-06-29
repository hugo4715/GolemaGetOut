package net.faiden.getout;

public enum MapGameInfos {

	AKALIA("Akalia", "akalia.yml");

	public String mapName;
	public String configName;

	/**
	 * Constructeur du MapInfos.
	 * 
	 * @param mapName
	 * @param configName
	 */
	private MapGameInfos(String mapName, String configName) {
		this.mapName = mapName;
		this.configName = configName;
	}

	/**
	 * Récupérer le nom de la Map.
	 * 
	 * @return
	 */
	public String getMapName() {
		return mapName;
	}

	/**
	 * Récupérer le nom de la Config.
	 * 
	 * @return
	 */
	public String getConfigName() {
		return configName;
	}
}