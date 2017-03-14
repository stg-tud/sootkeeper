package de.tud.cs.peaks.results.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.tud.cs.peaks.misc.PeaksHost;

public class ResultHelper {
	
	/**
	 * Sorts the result with increasing rating.
	 * @param result the list to sort.
	 * @return a sorted copy of the result.
	 */
	public List<PeaksHost> sort(Collection<? extends PeaksHost> result){
		List<PeaksHost> res = new ArrayList<PeaksHost>(result.size());
		res.addAll(result);
		
		Collections.sort(res, new Comparator<PeaksHost>() {

			@Override
			public int compare(PeaksHost host1, PeaksHost host2) {
				return Float.compare(host1.getRating(), host2.getRating());
			}
		});
		
		return res;
	}
	
	/**
	 * Calculates the number of PeaksHosts with a rating greater or equal to the threshold
	 * @param result
	 * @param threshold
	 * @return
	 */
	public int countHostsRatingGreaterOrEqual(Collection<? extends PeaksHost> result, double threshold){
		int count = 0;
		
		for(PeaksHost host : result){
			if(host.getRating() >= threshold)
				count++;
		}
		
		return count;
	}
	
	/**
	 * Calculates the number of PeaksHosts with a rating smaller to the threshold
	 * @param result
	 * @param threshold
	 * @return
	 */
	public int countHostsRatingSmaller(Collection<? extends PeaksHost> result, double threshold){
		
		return result.size() - countHostsRatingGreaterOrEqual(result, threshold);
	}
}
