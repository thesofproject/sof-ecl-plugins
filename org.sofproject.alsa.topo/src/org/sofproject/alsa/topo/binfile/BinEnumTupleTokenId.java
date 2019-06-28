/*
 * Copyright (c) 2018, Intel Corporation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the Intel Corporation nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.sofproject.alsa.topo.binfile;

import java.nio.ByteBuffer;

import org.sofproject.core.binfile.BinItem;

public class BinEnumTupleTokenId extends BinItem {

	// TODO: move token ids to separate plug-in queried by vendor id

	/**
	 * @formatter:off
	 */
	public enum TupleTokenId {
		BUF_SIZE(100),
		BUF_CAPS(101),

		DAI_DMAC_CONFIG(153),
		DAI_TYPE(154),
		DAI_INDEX(155),
		DAI_DIRECTION(156),

		SCHED_DEADLINE(200),
		SCHED_PRIORITY(201),
		SCHED_MIPS(202),
		SCHED_CORE(203),
		SCHED_FRAMES(204),
		SCHED_TIMER(205),

		VOLUME_RAMP_STEP_TYPE(250),
		VOLUME_RAMP_STEP_MS(251),

		SRC_RATE_IN(300),
		SRC_RATE_OUT(301),

		PCM_DMAC_CONFIG(353),

		COMP_PERIOD_SINK_COUNT(400),
		COMP_PERIOD_SOURCE_COUNT(401),
		COMP_FORMAT(402),
		COMP_PRELOAD_COUNT(403),

		INTEL_SSP_CLKS_CONTROL(500),
		INTEL_SSP_MCLK_ID(501),
		INTEL_SSP_SAMPLE_BITS(502),
		INTEL_SSP_FRAME_PULSE_WIDTH(503),
		INTEL_SSP_QUIRKS(504),
		INTEL_SSP_TDM_PADDING_PER_SLOT(505),

		INTEL_DMIC_DRIVER_VERSION(600),
		INTEL_DMIC_CLK_MIN(601),
		INTEL_DMIC_CLK_MAX(602),
		INTEL_DMIC_DUTY_MIN(603),
		INTEL_DMIC_DUTY_MAX(604),
		INTEL_DMIC_NUM_PDM_ACTIVE(605),
		INTEL_DMIC_SAMPLE_RATE(608),
		INTEL_DMIC_FIFO_WORD_LENGTH(609),

		INTEL_DMIC_PDM_CTRL_ID(700),
		INTEL_DMIC_PDM_MIC_A_Enable(701),
		INTEL_DMIC_PDM_MIC_B_Enable(702),
		INTEL_DMIC_PDM_POLARITY_A(703),
		INTEL_DMIC_PDM_POLARITY_B(704),
		INTEL_DMIC_PDM_CLK_EDGE(705),
		INTEL_DMIC_PDM_SKEW(706),

		TONE_SAMPLE_RATE(800),

		EFFECT_TYPE(900);

		private final int val;

		TupleTokenId(int val) {
			this.val = val;
		}

		public static TupleTokenId find(int val) {
			for (TupleTokenId bt : TupleTokenId.values()) {
				if (bt.val == val)
					return bt;
			}
			throw new IllegalArgumentException("Unknown TupleTokenId " + val);
		}

	}

	TupleTokenId value;

	public BinEnumTupleTokenId(String name) {
		super(name);
	}

	@Override
	public BinItem read(ByteBuffer bb) {
		super.read(bb);
		this.value = TupleTokenId.find(bb.getInt());
		return this;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public Object getRawValue() {
		return value.val;
	}

	@Override
	public String getValueString() {
		return value.name();
	}
}
