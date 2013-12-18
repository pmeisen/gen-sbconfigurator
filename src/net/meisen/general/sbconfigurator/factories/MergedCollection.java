package net.meisen.general.sbconfigurator.factories;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Implementation of a {@code FactoryBean} which can be used to merge a
 * {@code Collection} of {@code Collections} into one collection.
 * 
 * @see FactoryBean
 * @author pmeisen
 * 
 */
public class MergedCollection extends AbstractFactoryBean<Collection<?>> {

	@SuppressWarnings("rawtypes")
	private Class<? extends Collection> collectionClass = ArrayList.class;
	private Collection<Collection<?>> collections;

	@Override
	public Class<?> getObjectType() {
		return Collection.class;
	}

	/**
	 * Sets the class of the collection to be created, by default an
	 * {@code ArrayList} is used.
	 * 
	 * @param collectionClass
	 *            the class of the collection to be created
	 */
	@SuppressWarnings("rawtypes")
	public void setCollectionClass(
			final Class<? extends Collection> collectionClass) {
		this.collectionClass = collectionClass;
	}

	/**
	 * Set the collections to be merged.
	 * 
	 * @param collections
	 *            thec collections to be merged
	 */
	public void setCollections(final Collection<Collection<?>> collections) {
		this.collections = collections;
	}

	@Override
	protected Collection<?> createInstance() throws Exception {
		@SuppressWarnings("unchecked")
		final Collection<Object> mergedRes = (Collection<Object>) BeanUtils
				.instantiateClass(collectionClass);

		if (collections == null || collections.size() == 0) {
			// nothing to do
		} else {

			// add all the colletions together
			for (final Collection<?> collection : collections) {
				if (collection != null) {
					mergedRes.addAll(collection);
				}
			}
		}

		return mergedRes;
	}
}
