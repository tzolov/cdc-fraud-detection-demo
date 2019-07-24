package org.spring.cloud.demo.credit.card.transaction.generator;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Christian Tzolov
 */
@Route(value = "generator")
@SpringComponent
public class GeneratorControlView extends VerticalLayout {

	@Autowired
	private RecordGenerator recordGenerator;

	public boolean stopped = true;

	private Double fraudPercentage = 30d;

	private int minWaitSecond = 1;

	private int maxWaitSecond = 10;

	public GeneratorControlView() {

		NumberField minWaitField = new NumberField("Min Wait");
		minWaitField.setValue(1d);
		minWaitField.setMin(0);
		minWaitField.setMax(10);
		minWaitField.setHasControls(true);
		minWaitField.setSuffixComponent(new Span("sec"));
		minWaitField.addValueChangeListener(event -> this.minWaitSecond = event.getValue().intValue());

		NumberField maxWaitField = new NumberField("Max Wait");
		maxWaitField.setValue(10d);
		maxWaitField.setMin(1);
		maxWaitField.setMax(10);
		maxWaitField.setHasControls(true);
		maxWaitField.setSuffixComponent(new Span("sec"));
		maxWaitField.addValueChangeListener(event -> {
			this.maxWaitSecond = event.getValue().intValue();
			minWaitField.setMax(event.getValue() - 1);
			minWaitField.setValue(Math.min(event.getValue() - 1, minWaitField.getValue()));
		});

		NumberField fraudPercentageField = new NumberField("Fraud Ratio");
		fraudPercentageField.setValue(30d);
		fraudPercentageField.setMin(0);
		fraudPercentageField.setMax(100);
		fraudPercentageField.setHasControls(true);
		fraudPercentageField.setStep(5);
		fraudPercentageField.setSuffixComponent(new Span("%"));
		fraudPercentageField.addValueChangeListener(event -> this.fraudPercentage = event.getValue());

		Button startStopButton = new Button("Start", VaadinIcon.START_COG.create());
		startStopButton.addClickListener(e -> {
			this.stopped = !this.stopped;
			if (this.stopped == true) {
				startStopButton.setIcon(VaadinIcon.START_COG.create());
				startStopButton.setText("Start");
			}
			else {
				startStopButton.setIcon(VaadinIcon.STOP.create());
				startStopButton.setText("Stop");
				try {
					this.recordGenerator.start();
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		this.add(minWaitField);
		this.add(maxWaitField);
		this.add(fraudPercentageField);
		this.add(startStopButton);
	}

	public Double getFraudPercentage() {
		return fraudPercentage;
	}

	public boolean isStopped() {
		return stopped;
	}

	public int getMinWaitSecond() {
		return minWaitSecond;
	}

	public int getMaxWaitSecond() {
		return maxWaitSecond;
	}
}
