package net.meisen.general.sbconfigurator.api.transformer;

import java.io.InputStream;

import net.meisen.general.sbconfigurator.ConfigurationCoreSettings;

/**
 * A <code>ILoaderDefinition</code> is used to define a specific loading
 * behavior of a configuration. It can be used to define loading packages which
 * load any kind of plug-in for the configuration.
 * 
 * @author pmeisen
 * 
 */
public interface ILoaderDefinition {

	/**
	 * Gets a newly created <code>InputStream</code> to read the xslt transformer
	 * definition of this <code>ILoaderDefinition</code>.
	 * 
	 * @return a newly created <code>InputStream</code> to read the xslt
	 *         transformer definition of this <code>ILoaderDefinition</code>.
	 */
	public InputStream getXsltTransformerInputStream();

	/**
	 * Gets a newly created <code>InputStream</code> to read the xsd schema of
	 * this <code>ILoaderDefinition</code>.
	 * 
	 * @return a newly created <code>InputStream</code> to read the xsd schema of
	 *         this <code>ILoaderDefinition</code>.
	 */
	public InputStream getXsdSchemaInputStream();

	/**
	 * Checks if a schema is defined for this definition.
	 * 
	 * @return <code>true</code> if a schema is defined, otherwise
	 *         <code>false</code>
	 */
	public boolean hasXsdSchema();

	/**
	 * Gets the selector to be used to select the xml-files to be changed by this
	 * <code>ILoaderDefinition</code>.
	 * 
	 * @return the selector to be used to select the xml-files to be changed by
	 *         this <code>ILoaderDefinition</code>
	 */
	public String getSelector();

	/**
	 * Gets the context in which the selected files should be chosen.
	 * 
	 * @return the context in which the selected files should be chosen
	 */
	public Class<?> getContext();

	/**
	 * Checks if a context is defined for this definition.
	 * 
	 * @return <code>true</code> if a context is defined, otherwise
	 *         <code>false</code>
	 */
	public boolean hasContext();

	/**
	 * Defines if it is possible to override beans by the loader created for this
	 * <code>ILoaderDefinition</code>.
	 * 
	 * @return <code>true</code> if bean overriding is allowed, otherwise
	 *         <code>false</code>
	 */
	public boolean isBeanOverridingAllowed();

	/**
	 * Defines if the XML read during loading should be validated. This setting
	 * can be overruled by the general configuration setting.
	 * 
	 * @return <code>true</code> if the XML should be validated during the loading
	 *         process or not
	 * 
	 * @see ConfigurationCoreSettings#isConfigurationValidationEnabled()
	 */
	public boolean isValidationEnabled();

	/**
	 * Defines if the defined selector should select files from the class-path (
	 * <code>true</code>) or not (<code>false</code>).
	 * 
	 * @return <code>true</code> if the selected should search on the class-path,
	 *         otherwise <code>false</code>
	 */
	public boolean isLoadFromClassPath();

	/**
	 * Defines if the defined selector should select files from the current
	 * working directory and all it's sub-directories ( <code>true</code>) or not
	 * (<code>false</code>).
	 * 
	 * @return <code>true</code> if the selector should search in the current
	 *         working-directory (and all sub-directories), otherwise
	 *         <code>false</code>
	 */
	public boolean isLoadFromWorkingDir();
}
