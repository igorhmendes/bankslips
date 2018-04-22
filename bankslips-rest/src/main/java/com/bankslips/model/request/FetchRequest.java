package com.bankslips.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FetchRequest<T> extends Request {
	private static final long serialVersionUID = 6700802282272165275L;

	private T example;
	private Integer page;
	private Integer size;
}
