package net.meisen.general.sbconfigurator.config.placeholder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import net.meisen.general.genmisc.resources.ResourceInfo;
import net.meisen.general.genmisc.types.Objects;
import net.meisen.general.genmisc.types.Streams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.Constants;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

/**
 * {@link PropertyPlaceholderConfigurer} extension which enables the usage of
 * selectors, when defining properties (besides the typical usage of locations
 * or local values). To define the properties you can set different 'properties'
 * of the bean, those are:
 * <ul>
 * <li>properties</li>
 * <li>propertiesArray (define as list, i.e. &lt;list&gt;...&lt;/list&gt;)</li>
 * <li>location</li>
 * <li>locations (define as list)</li>
 * <li>locationSelector</li>
 * <li>locationSelectors (define as list)</li>
 * </ul>
 * It is possible to combine local properties with locations or
 * locationSelectors. It is not possible to combine locations with
 * locationSelectors, if this is done, the latter - concerning the definition
 * order - will be loaded.<br/>
 * Furthermore it is possible to load properties form other defined
 * <code>SpringPropertyHolder</code> instances, i.e. when retrieving the
 * properties from an instance it is possible to load the properties from all
 * other instances (see {@link #setOtherHolderOverride(boolean)}).<br/>
 * Last but not least, it is still possible to define how <code>System</code>
 * properties should be handled (see
 * {@link PropertyPlaceholderConfigurer#SYSTEM_PROPERTIES_MODE_NEVER
 * SYSTEM_PROPERTIES_MODE_NEVER},
 * {@link PropertyPlaceholderConfigurer#SYSTEM_PROPERTIES_MODE_FALLBACK
 * SYSTEM_PROPERTIES_MODE_FALLBACK},
 * {@link PropertyPlaceholderConfigurer#SYSTEM_PROPERTIES_MODE_OVERRIDE
 * SYSTEM_PROPERTIES_MODE_OVERRIDE}). <br/>
 * <br/>
 * Here are some examples on how you can define properties using the
 * <code>SpringPropertyHolder</code>.
 * 
 * <pre>
 * <b>Define local properties</b>
 * &lt;bean class=&quot;net.meisen.general.sbconfigurator.config.placeholder.SpringPropertyHolder&quot;&gt;
 *   &lt;property name=&quot;properties&quot;&gt;
 *     &lt;value&gt;
 *       testValue1=1st-value1
 *       testValue2=1st-value2
 *     &lt;/value&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * 
 * <b>Load properties from a file</b>
 * &lt;bean class=&quot;net.meisen.general.sbconfigurator.config.placeholder.SpringPropertyHolder&quot;&gt;
 *   &lt;property name=&quot;location&quot; value=&quot;classpath:net/meisen/properties/sample.properties&quot; /&gt;
 * &lt;/bean&gt;
 * 
 * <b>Load properties via a selector</b>
 * &lt;bean class=&quot;net.meisen.general.sbconfigurator.config.placeholder.SpringPropertyHolder&quot;&gt;
 *   &lt;property name=&quot;locationSelector&quot; value=&quot;properties/sample.properties&quot; /&gt;
 * &lt;/bean&gt;
 * 
 * <b>Load properties via several selectors</b>
 * &lt;bean class=&quot;net.meisen.general.sbconfigurator.config.placeholder.SpringPropertyHolder&quot;&gt;
 *   &lt;property name=&quot;locationSelector&quot; /&gt;
 *     &lt;list&gt;
 *       &lt;value&gt;properties/sample1.properties&lt;value/&gt;
 *       &lt;value&gt;properties/sample2.properties&lt;value/&gt;
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * 
 * <b>Define local properties and define additional properties from a file, ignore <code>System</code> properties</b>
 * &lt;bean class=&quot;net.meisen.general.sbconfigurator.config.placeholder.SpringPropertyHolder&quot;&gt;
 *   &lt;property name=&quot;systemPropertiesModeName&quot; value=&quot;SYSTEM_PROPERTIES_MODE_NEVER&quot; /&gt;
 *   &lt;property name=&quot;localOverride&quot; value=&quot;true&quot; /&gt;
 *   &lt;property name=&quot;location&quot; value=&quot;classpath:net/meisen/properties/sample.properties&quot; /&gt;
 *   &lt;property name=&quot;properties&quot;&gt;
 *     &lt;value&gt;
 *       testValue1=1st-value1
 *       testValue2=1st-value2
 *     &lt;/value&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * @author pmeisen
 * 
 * @see net.meisen.general.genmisc.resources.Resource#getAvailableResources(String)
 *      Resource.getAvailableResources(String)
 * @see ResourceInfo
 */
public class SpringPropertyHolder extends PropertyPlaceholderConfigurer {
	private final static Logger LOG = LoggerFactory
			.getLogger(SpringPropertyHolder.class);

	private static final Constants constants = new Constants(
			PropertyPlaceholderConfigurer.class);

	private boolean otherHolderOverride = true;

	private int systemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

	@Autowired(required = false)
	List<SpringPropertyHolder> propertyHolders = new ArrayList<SpringPropertyHolder>();

	/**
	 * Default constructor, which sets the
	 * {@link #setIgnoreUnresolvablePlaceholders(boolean)} to <code>true</code>.
	 */
	public SpringPropertyHolder() {
		// change the default behavior to true
		setIgnoreUnresolvablePlaceholders(true);
	}

	@Override
	protected Properties mergeProperties() throws IOException {
		return getProperties(true);
	}

	/**
	 * Get all the properties defined by <code>this</code> holder and the once
	 * defined by other <code>SpringPropertyHolder</code> instances.
	 * 
	 * @return all the defined properties
	 * 
	 * @throws IOException
	 *           if a file resource is defined but cannot be read or accessed
	 */
	public Properties getProperties() throws IOException {
		return getProperties(true);
	}

	/**
	 * Get all the properties defined by <code>this</code> holder.
	 * 
	 * @param inclOtherReplacer
	 *          <code>true</code> to include the properties defined by other
	 *          <code>SpringPropertyHolder</code> instances, otherwise
	 *          <code>false</code>
	 * 
	 * @return the defined properties
	 * 
	 * @throws IOException
	 *           if a file resource is defined but cannot be read or accessed
	 */
	public Properties getProperties(final boolean inclOtherReplacer)
			throws IOException {
		final Properties result = new Properties();

		// get the other replacer first if they don't overwrite
		if (!isOtherHolderOverride() && inclOtherReplacer) {
			CollectionUtils
					.mergePropertiesIntoMap(getOtherHolderProperties(), result);
		}

		// fallback means use them if no others are there, therefore add them first
		if (systemPropertiesMode == SYSTEM_PROPERTIES_MODE_FALLBACK) {
			CollectionUtils.mergePropertiesIntoMap(System.getProperties(), result);
		}

		// now override all the system once (if there are any) with the merged
		// properties
		CollectionUtils.mergePropertiesIntoMap(super.mergeProperties(), result);

		// if system properties should override to it now
		if (systemPropertiesMode == SYSTEM_PROPERTIES_MODE_OVERRIDE) {
			CollectionUtils.mergePropertiesIntoMap(System.getProperties(), result);
		}

		// other replacer can overwrite this one
		if (isOtherHolderOverride() && inclOtherReplacer) {
			CollectionUtils
					.mergePropertiesIntoMap(getOtherHolderProperties(), result);
		}

		// return all properties
		return result;
	}

	/**
	 * Gets all the properties of the other defined
	 * <code>SpringPropertyHolder</code> instances, whereby the definition-order
	 * matters concerning the overriding of properties, i.e. the latter definition
	 * overrides the earlier (if the same property is defined several times).
	 * 
	 * @return the properties defined by the other
	 *         <code>SpringPropertyHolder</code> instances
	 * 
	 * @throws IOException
	 *           if a file resource is defined but cannot be read or accessed
	 */
	protected Properties getOtherHolderProperties() throws IOException {
		final Properties result = new Properties();

		for (final SpringPropertyHolder propertyHolder : propertyHolders) {
			final int oldSystemPropertyMode = propertyHolder
					.getSystemPropertiesMode();
			propertyHolder.setSystemPropertiesMode(SYSTEM_PROPERTIES_MODE_NEVER);
			CollectionUtils.mergePropertiesIntoMap(
					propertyHolder.getProperties(false), result);
			propertyHolder.setSystemPropertiesMode(oldSystemPropertyMode);
		}

		return result;
	}

	/**
	 * Gets the defined <code>SystemPropertiesMode</code> (see
	 * {@link PropertyPlaceholderConfigurer#SYSTEM_PROPERTIES_MODE_NEVER
	 * SYSTEM_PROPERTIES_MODE_NEVER},
	 * {@link PropertyPlaceholderConfigurer#SYSTEM_PROPERTIES_MODE_FALLBACK
	 * SYSTEM_PROPERTIES_MODE_FALLBACK},
	 * {@link PropertyPlaceholderConfigurer#SYSTEM_PROPERTIES_MODE_OVERRIDE
	 * SYSTEM_PROPERTIES_MODE_OVERRIDE}).
	 * 
	 * @return the number of the defined mode
	 * 
	 * @see #setSystemPropertiesMode
	 * @see #setSystemPropertiesModeName
	 */
	public int getSystemPropertiesMode() {
		return systemPropertiesMode;
	}

	@Override
	public void setSystemPropertiesModeName(final String constantName)
			throws IllegalArgumentException {
		super.setSystemPropertiesModeName(constantName);
		this.systemPropertiesMode = constants.asNumber(constantName).intValue();
	}

	@Override
	public void setSystemPropertiesMode(final int systemPropertiesMode) {
		super.setSystemPropertiesMode(systemPropertiesMode);
		this.systemPropertiesMode = systemPropertiesMode;
	}

	/**
	 * Defines one selector for <code>this</code> instance. All other pre-defined
	 * selectors are removed.
	 * 
	 * @param locationSelector
	 *          the selector to be used
	 */
	public void setLocationSelector(final String locationSelector) {
		setLocationSelectors(new String[] { locationSelector });
	}

	/**
	 * Defines several selectors for <code>this</code> instance. All other
	 * pre-defined selectors are removed.
	 * 
	 * @param locationSelectors
	 *          the array of selectors to use
	 */
	public void setLocationSelectors(final String[] locationSelectors) {

		// nothing to do if we don't have anything defined
		if (locationSelectors == null || locationSelectors.length < 1) {
			return;
		}

		// create a list of all the locations
		final Set<Resource> locations = new LinkedHashSet<Resource>();

		// get through each locationSelector
		for (final String locationSelector : locationSelectors) {

			// check the locationSelector
			if (Objects.empty(locationSelector)) {
				if (LOG.isWarnEnabled()) {
					LOG.warn("The specified locationSelector is empty and therefore cannot be resolved");
				}
				continue;
			}

			// resolve the selector
			final Collection<ResourceInfo> resInfos = net.meisen.general.genmisc.resources.Resource
					.getResources(locationSelector, true, false);
			if (LOG.isTraceEnabled()) {
				LOG.trace("Found '" + resInfos.size()
						+ "' properties to be loaded by the selector: '" + locationSelector
						+ "'" + (resInfos.size() > 0 ? " (" + resInfos + ")" : ""));
			}

			// get all the found resource infos
			for (final ResourceInfo resInfo : resInfos) {

				// make sure we have a file
				if (!resInfo.isFile()) {
					if (LOG.isInfoEnabled()) {
						LOG.info("Skipping resource '"
								+ resInfo
								+ "', it was selected by the propertySelector, but isn't a file.");
					}
					continue;
				}

				// get the InputStream
				final InputStream resIo = net.meisen.general.genmisc.resources.Resource
						.getResourceAsStream(resInfo);

				// transform the stream to a byte-array
				try {
					final byte[] byteArray = Streams.copyStreamToByteArray(resIo);
					locations.add(new ByteArrayResource(byteArray));
				} catch (final IOException e) {
					if (LOG.isWarnEnabled()) {
						LOG.warn("Skipping resource '" + resInfo
								+ "', unable to access the resource.", e);
					}
				}
			}
		}

		// set the loaded locations
		setLocations(locations.toArray(new Resource[] {}));
	}

	/**
	 * Defines if the properties of other <code>SpringPropertyHolder</code>
	 * instances override the properties of this one (if defined to load at all).
	 * 
	 * @return <code>true</code> if the properties of the
	 *         <code>SpringPropertyHolder</code> instances override the once
	 *         defined by <code>this</code>, otherwise <code>false</code>
	 */
	public boolean isOtherHolderOverride() {
		return otherHolderOverride;
	}

	/**
	 * Defines if the properties of other <code>SpringPropertyHolder</code>
	 * instances override the properties of this one.
	 * 
	 * @param otherHolderOverride
	 *          <code>true</code> if the properties of the
	 *          <code>SpringPropertyHolder</code> instances should override the
	 *          once defined by <code>this</code>, otherwise <code>false</code>
	 */
	public void setOtherHolderOverride(boolean otherHolderOverride) {
		this.otherHolderOverride = otherHolderOverride;
	}
}
