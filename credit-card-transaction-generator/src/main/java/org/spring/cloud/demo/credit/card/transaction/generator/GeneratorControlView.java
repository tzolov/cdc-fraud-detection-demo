package org.spring.cloud.demo.credit.card.transaction.generator;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;

/**
 * @author Christian Tzolov
 */
@Route(value = "generator")
public class GeneratorControlView extends VerticalLayout {

	private RecordGenerator recordGenerator;

	public AtomicBoolean stopped = new AtomicBoolean(true);

	private AtomicLong fraudPercentage = new AtomicLong(30);

	private AtomicLong minWaitSecond = new AtomicLong(1);

	private AtomicLong maxWaitSecond = new AtomicLong(10);

	// Constructor arguments are autowired
	public GeneratorControlView(RecordGenerator recordGenerator) {

		this.recordGenerator = recordGenerator;

		NumberField minWaitField = new NumberField("Min Wait");
		minWaitField.setValue(1d);
		minWaitField.setMin(0);
		minWaitField.setMax(10);
		minWaitField.setHasControls(true);
		minWaitField.setSuffixComponent(new Span("sec"));
		minWaitField.addValueChangeListener(event -> this.minWaitSecond.getAndSet(event.getValue().longValue()));

		NumberField maxWaitField = new NumberField("Max Wait");
		maxWaitField.setValue(10d);
		maxWaitField.setMin(1);
		maxWaitField.setMax(10);
		maxWaitField.setHasControls(true);
		maxWaitField.setSuffixComponent(new Span("sec"));
		maxWaitField.addValueChangeListener(event -> {
			this.maxWaitSecond.getAndSet(event.getValue().longValue());
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
		fraudPercentageField.addValueChangeListener(
				event -> this.fraudPercentage.getAndSet(event.getValue().longValue()));

		Button startStopButton = new Button("Start", VaadinIcon.START_COG.create());
		startStopButton.addClickListener(e -> {
			this.stopped.getAndSet(!this.stopped.get());
			if (this.stopped.get()) {
				startStopButton.setIcon(VaadinIcon.START_COG.create());
				startStopButton.setText("Start");
			}
			else {
				startStopButton.setIcon(VaadinIcon.STOP.create());
				startStopButton.setText("Stop");
				try {
					this.recordGenerator.start(this);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		this.add(new H3("Credit Card Transaction Generator"));
		this.add(minWaitField);
		this.add(maxWaitField);
		this.add(fraudPercentageField);
		this.add(startStopButton);
	}

	public long getFraudPercentage() {
		return fraudPercentage.get();
	}

	public boolean isStopped() {
		return stopped.get();
	}

	public long getMinWaitSecond() {
		return minWaitSecond.get();
	}

	public long getMaxWaitSecond() {
		return maxWaitSecond.get();
	}
}
