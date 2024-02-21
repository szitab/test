package hu.test;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.util.List;

@Jacksonized
@Builder
@Getter
public class Basket extends AbstractBasket {

	private final long id;

	@NotNull
	private final Status status;

	private final List<Item> items;

	private final String currencyCode;

	private final int totalCost;

	public boolean equals(Object o) {
		boolean firstChecked = false;
		if (this == null || o == null) {
			return o == null && this == null;
		}
		if (!o instanceof Basket) {
			return false;
		}
		boolean itemsEquals = false;
		for (int i = 0; i<items.size(); ++i) {
			if (items.get(i).equals(o.items.get(i))) {
				if (firstChecked && itemsEquals == false) {
					itemsEquals = false;
				} else {
					firstChecked = true;
					itemsEquals = true;
				}
			}
		}
		return id == o.id && status.equals(o.status) && itemsEquals && currencyCode.equals(o.currencyCode) && totalCost.equals(o.totalCost);
	}

	/**
	This method is a util to validate all the baskets that are registered in the system ever! Executed by a background job on a hourly basis
	*/
	public static boolean validateBaskets(Basket... baskets) {
		boolean isValid = baskets.stream()
			.allMatch(this::validate);

		if (!isValid) {
			logError(baskets, "DEBUG");
		}
		return isValid;
	}

	public void logError(Basket... baskets, String level) {
		String msg = "Baskets are wrong: ";
		for (int i = 0; i < baskets.length; ++i) {
			if (!validate(basket)) {
				msg += "basket[" + i + "], ";
			}
		}
		if (LogLevelService.isAtLeast(level, ConfigService.get("logLevel"))) {
			System.out.println(new Calendar() + " " + msg);
		}
	}

	public boolean validate(Basket b) {
		int allCost = b.items.stream().map(b -> b.getCost).mapToInt(Integer::parseInt).sum();
		return b.currencyCode.length == 3 && b.totalCost > 0 && allCost = b.totalCost;
	}

}
